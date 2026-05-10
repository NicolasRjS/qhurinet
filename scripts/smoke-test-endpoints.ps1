param(
    [string]$BaseUrl = "http://localhost:8083"
)

$ErrorActionPreference = "Stop"
$Results = New-Object System.Collections.Generic.List[object]
$OutDir = Join-Path (Get-Location) "target\smoke-test"
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

function Add-Result {
    param([string]$Name, [string]$Method, [string]$Path, [int]$Status, [bool]$Ok, [string]$Detail)
    $Results.Add([pscustomobject]@{
        name = $Name
        method = $Method
        path = $Path
        status = $Status
        ok = $Ok
        detail = $Detail
    }) | Out-Null
}

function Convert-Response {
    param([string]$Content)
    if ([string]::IsNullOrWhiteSpace($Content)) {
        return $null
    }
    $trimmed = $Content.Trim()
    if ($trimmed.StartsWith("{") -or $trimmed.StartsWith("[")) {
        return $trimmed | ConvertFrom-Json
    }
    return $Content
}

function Invoke-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        [object]$Body = $null,
        [int[]]$Expected = @(200, 201, 202, 204),
        [string]$Token = $script:AccessToken,
        [string]$ContentType = "application/json"
    )

    $headers = @{}
    if (-not [string]::IsNullOrWhiteSpace($Token)) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $params = @{
        Method = $Method
        Uri = "$BaseUrl$Path"
        Headers = $headers
        UseBasicParsing = $true
        TimeoutSec = 30
    }
    if ($null -ne $Body) {
        $params["ContentType"] = $ContentType
        $params["Body"] = ($Body | ConvertTo-Json -Depth 20)
    }

    try {
        $response = Invoke-WebRequest @params
        $status = [int]$response.StatusCode
        $ok = $Expected -contains $status
        Add-Result $Name $Method $Path $status $ok $response.Content
        return Convert-Response $response.Content
    } catch {
        $status = 0
        $content = $_.Exception.Message
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $content = $reader.ReadToEnd()
            } catch {
                $content = $_.Exception.Message
            }
        }
        Add-Result $Name $Method $Path $status ($Expected -contains $status) $content
        return $null
    }
}

function Invoke-Multipart {
    param([string]$Name, [string]$Path, [string]$FilePath, [int[]]$Expected = @(200, 201, 202))
    $output = Join-Path $OutDir ("curl-" + ($Name -replace '[^a-zA-Z0-9_-]', '-') + ".out")
    $status = & curl.exe -s -o $output -w "%{http_code}" -H "Authorization: Bearer $script:AccessToken" -F "file=@$FilePath;type=image/png" "$BaseUrl$Path"
    $code = [int]$status
    $detail = ""
    if (Test-Path $output) {
        $detail = Get-Content $output -Raw
    }
    Add-Result $Name "POST" $Path $code ($Expected -contains $code) $detail
    return Convert-Response $detail
}

function Invoke-Sse {
    param([string]$Name, [string]$Path)
    $output = Join-Path $OutDir "sse.out"
    $status = & curl.exe --max-time 7 -s -o $output -w "%{http_code}" -H "Authorization: Bearer $script:AccessToken" "$BaseUrl$Path"
    $code = [int]$status
    $detail = ""
    if (Test-Path $output) {
        $detail = Get-Content $output -Raw
    }
    Add-Result $Name "GET" $Path $code ($code -eq 200 -and $detail -match "event:ubicacion") $detail
}

function First-Item($value) {
    return @($value)[0]
}

$png = Join-Path $OutDir "sample.png"
[Convert]::FromBase64String("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII=") | Set-Content -Encoding Byte $png

$login = Invoke-Api "auth.login" "POST" "/auth/login" @{ username = "admin"; password = "admin123" } -Token $null
$script:AccessToken = $login.accessToken
$refreshToken = $login.refreshToken
if ([string]::IsNullOrWhiteSpace($script:AccessToken)) {
    throw "No se pudo obtener accessToken de admin/admin123"
}

Invoke-Api "auth.refresh" "POST" "/auth/refresh" @{ refreshToken = $refreshToken } | Out-Null
Invoke-Api "auth.social-login" "POST" "/auth/social-login" @{
    provider = "google"; email = "social.smoke@qhurinet.test"; nombre = "Social Smoke"; tipoCuenta = "GENERADOR"; roles = @("GENERADOR")
} -Token $null | Out-Null

$usuarios = @(foreach ($u in (Invoke-Api "usuarios.lista" "GET" "/api/usuarios/lista")) { $u })
$admin = $usuarios | Where-Object { $_.username -eq "admin" } | Select-Object -First 1
$generador = $usuarios | Where-Object { $_.username -eq "generador" } | Select-Object -First 1
$bodega = $usuarios | Where-Object { $_.username -eq "bodega" } | Select-Object -First 1
$recolector = $usuarios | Where-Object { $_.username -eq "recolector" } | Select-Object -First 1

Invoke-Api "usuarios.get" "GET" "/api/usuarios/$($generador.id)" | Out-Null
Invoke-Api "usuarios.ranking" "GET" "/api/usuarios/ranking" | Out-Null
Invoke-Api "usuarios.perfil-recolector" "GET" "/api/usuarios/$($recolector.id)/perfil-recolector" | Out-Null
Invoke-Api "usuarios.puntos" "GET" "/api/usuarios/$($generador.id)/puntos" | Out-Null
Invoke-Api "usuarios.estadisticas-resumen" "GET" "/api/usuarios/$($generador.id)/estadisticas/resumen" | Out-Null
Invoke-Api "usuarios.estadisticas" "GET" "/api/usuarios/$($generador.id)/estadisticas?periodo_meses=6" | Out-Null
Invoke-Api "usuarios.kg-por-mes" "GET" "/api/usuarios/$($generador.id)/estadisticas/kg-por-mes" -Expected @(200, 404) | Out-Null

$stamp = [DateTimeOffset]::Now.ToUnixTimeMilliseconds()
$tempUser = Invoke-Api "usuarios.nuevo" "POST" "/api/usuarios/nuevo" @{
    nombre = "QA Usuario $stamp"; email = "qauser$stamp@qhurinet.test"; username = "qauser$stamp"; passwordHash = "qhuri123";
    telefono = "900000000"; fotoUrl = "/uploads/mock/qa.jpg"; descripcion = "Temporal"; roles = @("GENERADOR");
    tipoCuenta = "GENERADOR"; proveedorAuth = "local"; disponible = $true; verificado = $true; puntosTotales = 10; nivelParticipacion = "Bronce"
}
Invoke-Api "usuarios.actualiza" "PUT" "/api/usuarios/actualiza" @{
    id = $tempUser.id; nombre = "QA Usuario Editado"; email = $tempUser.email; username = $tempUser.username; telefono = "911111111";
    fotoUrl = "/uploads/mock/editado.jpg"; descripcion = "Temporal editado"; roles = @("GENERADOR"); tipoCuenta = "GENERADOR";
    proveedorAuth = "local"; disponible = $true; verificado = $true; puntosTotales = 20; nivelParticipacion = "Bronce"
} | Out-Null
Invoke-Api "usuarios.foto-url" "PATCH" "/api/usuarios/$($tempUser.id)/foto-url" @{ url = "/uploads/mock/foto-url.jpg" } | Out-Null
Invoke-Multipart "usuarios.foto" "/api/usuarios/$($tempUser.id)/foto" $png | Out-Null

$matTemp = Invoke-Api "materiales.nuevo" "POST" "/api/materiales/nuevo" @{
    nombre = "QA Material $stamp"; categoria = "plastico"; descripcion = "Material temporal"; puntosPorKg = 1.50
}
Invoke-Api "materiales.lista" "GET" "/api/materiales/lista" | Out-Null
Invoke-Api "materiales.get" "GET" "/api/materiales/$($matTemp.id)" | Out-Null
Invoke-Api "materiales.actualiza" "PUT" "/api/materiales/actualiza" @{
    id = $matTemp.id; nombre = $matTemp.nombre; categoria = "plastico"; descripcion = "Material temporal editado"; puntosPorKg = 1.75
} | Out-Null
Invoke-Api "materiales.top5" "GET" "/api/materiales/top5" | Out-Null
Invoke-Api "materiales.sugerencia" "GET" "/api/materiales/sugerencia?texto=Plastico" | Out-Null
Invoke-Api "materiales.clasificar" "POST" "/api/materiales/clasificar" @{ texto = "botellas plasticas PET y tapas" } | Out-Null

$pubTemp = Invoke-Api "publicaciones.nuevo" "POST" "/api/publicaciones/nuevo" @{
    idUsuario = $generador.id; titulo = "QA Publicacion $stamp"; observaciones = "Botellas plasticas PET limpias";
    estado = "activa"; latitud = -12.045; longitud = -77.040; direccionReferencia = "QA Lima";
    fechaDisponibilidad = (Get-Date).AddDays(4).ToString("yyyy-MM-dd"); imagenesJson = "/uploads/mock/pub.jpg"
}
Invoke-Api "publicaciones.lista" "GET" "/api/publicaciones/lista" | Out-Null
Invoke-Api "publicaciones.get" "GET" "/api/publicaciones/$($pubTemp.id)" | Out-Null
Invoke-Api "publicaciones.actualiza" "PUT" "/api/publicaciones/actualiza" @{
    id = $pubTemp.id; idUsuario = $generador.id; titulo = "QA Publicacion editada $stamp"; observaciones = "Carton y plastico";
    estado = "activa"; latitud = -12.045; longitud = -77.040; direccionReferencia = "QA Lima editado";
    fechaDisponibilidad = (Get-Date).AddDays(5).ToString("yyyy-MM-dd"); imagenesJson = "/uploads/mock/pub-edit.jpg"
} | Out-Null
Invoke-Api "publicaciones.evidencia-url" "PATCH" "/api/publicaciones/$($pubTemp.id)/evidencia-url" @{ url = "/uploads/mock/evidencia.jpg" } | Out-Null
Invoke-Multipart "publicaciones.evidencia" "/api/publicaciones/$($pubTemp.id)/evidencia" $png | Out-Null
Invoke-Api "publicaciones.clasificar-materiales" "POST" "/api/publicaciones/$($pubTemp.id)/clasificar-materiales" | Out-Null
Invoke-Api "publicaciones.buscar" "GET" "/api/publicaciones/buscar?q=Botellas" | Out-Null
Invoke-Api "publicaciones.mapa" "GET" "/api/publicaciones/mapa?lat=-12.046&lng=-77.043&radio_km=20&material=Plastico" | Out-Null
Invoke-Api "publicaciones.cercanas" "GET" "/api/publicaciones/cercanas?lat=-12.046&lng=-77.043&radio=20" | Out-Null
Invoke-Api "publicaciones.por-categoria" "GET" "/api/publicaciones/por-categoria/plastico" | Out-Null
Invoke-Api "publicaciones.historial-materiales" "GET" "/api/publicaciones/usuario/$($generador.id)/historial-materiales" | Out-Null

$pm = Invoke-Api "publicaciones-materiales.nuevo" "POST" "/api/publicaciones-materiales/nuevo" @{
    idPublicacion = $pubTemp.id; idMaterial = $matTemp.id; cantidad = 4.50; unidad = "kg"
}
Invoke-Api "publicaciones-materiales.lista" "GET" "/api/publicaciones-materiales/lista" | Out-Null
Invoke-Api "publicaciones-materiales.get" "GET" "/api/publicaciones-materiales/$($pubTemp.id)/$($matTemp.id)" | Out-Null
Invoke-Api "publicaciones-materiales.actualiza" "PUT" "/api/publicaciones-materiales/actualiza" @{
    idPublicacion = $pubTemp.id; idMaterial = $matTemp.id; cantidad = 5.00; unidad = "kg"
} | Out-Null

$fechaProgramada = (Get-Date).AddDays(2).ToString("yyyy-MM-ddTHH:mm:ss")
$recTemp = Invoke-Api "recolecciones.nuevo" "POST" "/api/recolecciones/nuevo" @{
    idPublicacion = $pubTemp.id; idRecolector = $recolector.id; estado = "programada"; fechaProgramada = $fechaProgramada;
    prioritaria = $false; qrValidado = $false; latRecolector = -12.047; lngRecolector = -77.041
}
Invoke-Api "recolecciones.lista" "GET" "/api/recolecciones/lista" | Out-Null
Invoke-Api "recolecciones.get" "GET" "/api/recolecciones/$($recTemp.id)" | Out-Null
Invoke-Api "recolecciones.actualiza" "PUT" "/api/recolecciones/actualiza" @{
    id = $recTemp.id; idPublicacion = $pubTemp.id; idRecolector = $recolector.id; estado = "programada";
    fechaProgramada = (Get-Date).AddDays(3).ToString("yyyy-MM-ddTHH:mm:ss"); prioritaria = $true;
    codigoQr = $recTemp.codigoQr; qrValidado = $false; latRecolector = -12.047; lngRecolector = -77.041
} | Out-Null
Invoke-Api "recolecciones.csv" "GET" "/api/recolecciones/$($recTemp.id)/comprobante.csv" | Out-Null
Invoke-Api "recolecciones.pdf" "GET" "/api/recolecciones/$($recTemp.id)/comprobante.pdf" | Out-Null
Invoke-Api "recolecciones.historial-csv" "GET" "/api/recolecciones/historial/$($generador.id)/comprobante.csv" | Out-Null
Invoke-Api "recolecciones.reprogramar" "PATCH" "/api/recolecciones/$($recTemp.id)/reprogramar" @{ fechaProgramada = (Get-Date).AddDays(4).ToString("yyyy-MM-ddTHH:mm:ss") } | Out-Null
Invoke-Api "recolecciones.prioridad" "PATCH" "/api/recolecciones/$($recTemp.id)/prioridad?prioritaria=true" | Out-Null
Invoke-Api "recolecciones.incidencia" "PATCH" "/api/recolecciones/$($recTemp.id)/incidencia" @{ descripcion = "Incidencia smoke"; evidenciaUrl = "/uploads/mock/incidencia.jpg"; estado = "abierta" } | Out-Null
Invoke-Api "recolecciones.actualizar-ubicacion" "PATCH" "/api/recolecciones/$($recTemp.id)/ubicacion-recolector" @{ latRecolector = -12.048; lngRecolector = -77.042 } | Out-Null
Invoke-Api "recolecciones.obtener-ubicacion" "GET" "/api/recolecciones/$($recTemp.id)/ubicacion-recolector" | Out-Null
Invoke-Sse "recolecciones.sse" "/api/recolecciones/$($recTemp.id)/ubicacion-recolector/stream"
Invoke-Api "recolecciones.ubicaciones" "GET" "/api/recolecciones/$($recTemp.id)/ubicaciones-recolector" | Out-Null
$qr = Invoke-Api "recolecciones.qr" "GET" "/api/recolecciones/$($recTemp.id)/qr"
Invoke-Api "recolecciones.validar-qr" "POST" "/api/recolecciones/$($recTemp.id)/validar-qr" @{ codigoQr = $qr.codigoQr } | Out-Null
Invoke-Api "recolecciones.historial" "GET" "/api/recolecciones/historial/$($generador.id)" | Out-Null
Invoke-Api "recolecciones.promedio-recolector" "GET" "/api/recolecciones/promedio-recolector/$($recolector.id)" | Out-Null
Invoke-Api "recolecciones.rango" "GET" "/api/recolecciones/rango?fechaIni=$((Get-Date).AddDays(-10).ToString('yyyy-MM-dd'))&fechaFin=$((Get-Date).AddDays(10).ToString('yyyy-MM-dd'))" | Out-Null
Invoke-Api "recolecciones.disponibilidad" "GET" "/api/recolecciones/disponibilidad?fecha=$((Get-Date).AddDays(4).ToString('yyyy-MM-dd'))" | Out-Null
Invoke-Api "recolecciones.pendientes-recolector" "GET" "/api/recolecciones/recolector/$($recolector.id)/pendientes" -Expected @(200, 404) | Out-Null
Invoke-Api "recolecciones.incidencias-usuario" "GET" "/api/recolecciones/incidencias/usuario/$($generador.id)" | Out-Null
Invoke-Api "recolecciones.actividades-detalle" "GET" "/api/recolecciones/actividades-detalle?idUsuario=$($generador.id)" | Out-Null

$calTemp = Invoke-Api "calificaciones.nuevo" "POST" "/api/calificaciones/nuevo" @{
    idRecoleccion = $recTemp.id; idAutor = $generador.id; puntuacion = 4; comentario = "Smoke calificacion"
}
Invoke-Api "calificaciones.lista" "GET" "/api/calificaciones/lista" | Out-Null
Invoke-Api "calificaciones.get" "GET" "/api/calificaciones/$($calTemp.id)" | Out-Null
Invoke-Api "calificaciones.actualiza" "PUT" "/api/calificaciones/actualiza" @{
    id = $calTemp.id; idRecoleccion = $recTemp.id; idAutor = $generador.id; puntuacion = 5; comentario = "Smoke calificacion editada"
} | Out-Null

$msgTemp = Invoke-Api "mensajes-chat.nuevo" "POST" "/api/mensajes-chat/nuevo" @{
    idRecoleccion = $recTemp.id; idRemitente = $generador.id; contenido = "Mensaje smoke"; leido = $false
}
Invoke-Api "mensajes-chat.lista" "GET" "/api/mensajes-chat/lista" | Out-Null
Invoke-Api "mensajes-chat.get" "GET" "/api/mensajes-chat/$($msgTemp.id)" | Out-Null
Invoke-Api "mensajes-chat.actualiza" "PUT" "/api/mensajes-chat/actualiza" @{
    id = $msgTemp.id; idRecoleccion = $recTemp.id; idRemitente = $generador.id; contenido = "Mensaje smoke editado"; leido = $true
} | Out-Null

$rutaTemp = Invoke-Api "rutas.nuevo" "POST" "/api/rutas/nuevo" @{
    idUsuario = $recolector.id; nombre = "Ruta Smoke $stamp"; puntosJson = "[]"; distanciaTotalKm = 3.20; tiempoEstimadoMin = 10; favorita = $false
}
Invoke-Api "rutas.lista" "GET" "/api/rutas/lista" | Out-Null
Invoke-Api "rutas.get" "GET" "/api/rutas/$($rutaTemp.id)" | Out-Null
Invoke-Api "rutas.actualiza" "PUT" "/api/rutas/actualiza" @{
    id = $rutaTemp.id; idUsuario = $recolector.id; nombre = "Ruta Smoke editada $stamp"; puntosJson = "[]"; distanciaTotalKm = 4.20; tiempoEstimadoMin = 13; favorita = $true
} | Out-Null
Invoke-Api "rutas.usuario" "GET" "/api/rutas/usuario/$($recolector.id)" | Out-Null
Invoke-Api "rutas.favoritas" "GET" "/api/rutas/usuario/$($recolector.id)/favoritas" | Out-Null
Invoke-Api "rutas.favorita" "PATCH" "/api/rutas/$($rutaTemp.id)/favorita?favorita=true" | Out-Null
Invoke-Api "rutas.optima" "POST" "/api/rutas/optima" @{
    idUsuario = $recolector.id; nombre = "Ruta Dijkstra Smoke"; origenId = "a"; destinoId = "c"; guardar = $false;
    puntos = @(
        @{ id = "a"; nombre = "A"; latitud = -12.046; longitud = -77.043 },
        @{ id = "b"; nombre = "B"; latitud = -12.060; longitud = -77.050 },
        @{ id = "c"; nombre = "C"; latitud = -12.071; longitud = -77.079 }
    )
} | Out-Null

$incTemp = Invoke-Api "incentivos.nuevo" "POST" "/api/incentivos/nuevo" @{
    tipo = "recompensa"; nombre = "Incentivo Smoke $stamp"; descripcion = "Temporal"; costoPuntos = 5; metaCantidad = 1; metaUnidad = "kg";
    fechaInicio = (Get-Date).AddDays(-1).ToString("yyyy-MM-dd"); fechaFin = (Get-Date).AddDays(10).ToString("yyyy-MM-dd"); stock = 10; activo = $true
}
Invoke-Api "incentivos.lista" "GET" "/api/incentivos/lista" | Out-Null
Invoke-Api "incentivos.get" "GET" "/api/incentivos/$($incTemp.id)" | Out-Null
Invoke-Api "incentivos.disponibles" "GET" "/api/incentivos/disponibles/$($generador.id)" | Out-Null
Invoke-Api "incentivos.actualiza" "PUT" "/api/incentivos/actualiza" @{
    id = $incTemp.id; tipo = "recompensa"; nombre = "Incentivo Smoke editado $stamp"; descripcion = "Temporal editado"; costoPuntos = 6;
    metaCantidad = 1; metaUnidad = "kg"; fechaInicio = (Get-Date).AddDays(-1).ToString("yyyy-MM-dd"); fechaFin = (Get-Date).AddDays(10).ToString("yyyy-MM-dd"); stock = 9; activo = $true
} | Out-Null

$uiTemp = Invoke-Api "usuarios-incentivos.nuevo" "POST" "/api/usuarios-incentivos/nuevo" @{
    idUsuario = $generador.id; idIncentivo = $incTemp.id; cantidadActual = 1; estado = "en_progreso"
}
Invoke-Api "usuarios-incentivos.lista" "GET" "/api/usuarios-incentivos/lista" | Out-Null
Invoke-Api "usuarios-incentivos.get" "GET" "/api/usuarios-incentivos/$($uiTemp.id)" | Out-Null
Invoke-Api "usuarios-incentivos.actualiza" "PUT" "/api/usuarios-incentivos/actualiza" @{
    id = $uiTemp.id; idUsuario = $generador.id; idIncentivo = $incTemp.id; cantidadActual = 2; estado = "completado"; completadoEn = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
} | Out-Null
Invoke-Api "usuarios-incentivos.progreso" "GET" "/api/usuarios-incentivos/progreso/$($generador.id)" | Out-Null
Invoke-Api "usuarios-incentivos.recordatorios" "GET" "/api/usuarios-incentivos/recordatorios/$($generador.id)" | Out-Null
Invoke-Api "usuarios-incentivos.canjear" "POST" "/api/usuarios-incentivos/canjear" @{ idUsuario = $generador.id; idIncentivo = $incTemp.id } -Expected @(200, 201, 400, 409) | Out-Null

$certTemp = Invoke-Api "certificados.nuevo" "POST" "/api/certificados/nuevo" @{
    idUsuario = $generador.id; nombre = "Certificado Smoke $stamp"; descripcion = "Temporal"; nivelDificultad = "facil"; puntosRequeridos = 10; urlPdf = "/uploads/mock/cert.pdf"
}
Invoke-Api "certificados.lista" "GET" "/api/certificados/lista" | Out-Null
Invoke-Api "certificados.por-dificultad" "GET" "/api/certificados/por-dificultad?nivel=facil" | Out-Null
Invoke-Api "certificados.get" "GET" "/api/certificados/$($certTemp.id)" | Out-Null
Invoke-Api "certificados.actualiza" "PUT" "/api/certificados/actualiza" @{
    id = $certTemp.id; idUsuario = $generador.id; nombre = "Certificado Smoke editado $stamp"; descripcion = "Temporal editado"; nivelDificultad = "medio"; puntosRequeridos = 20; urlPdf = "/uploads/mock/cert2.pdf"; fechaObtencion = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
} | Out-Null

$docTemp = Invoke-Api "documentos.nuevo" "POST" "/api/documentos-verificacion/nuevo" @{
    idUsuario = $generador.id; tipo = "dni-smoke"; urlArchivo = "/uploads/mock/dni-smoke.pdf"; estado = "pendiente"
}
Invoke-Api "documentos.lista" "GET" "/api/documentos-verificacion/lista" | Out-Null
Invoke-Api "documentos.get" "GET" "/api/documentos-verificacion/$($docTemp.id)" | Out-Null
Invoke-Api "documentos.actualiza" "PUT" "/api/documentos-verificacion/actualiza" @{
    id = $docTemp.id; idUsuario = $generador.id; tipo = "dni-smoke"; urlArchivo = "/uploads/mock/dni-smoke2.pdf"; estado = "aprobado"; motivoRechazo = $null
} | Out-Null
Invoke-Api "documentos.archivo-url" "PATCH" "/api/documentos-verificacion/$($docTemp.id)/archivo-url" @{ url = "/uploads/mock/doc-url.pdf" } | Out-Null
Invoke-Multipart "documentos.archivo" "/api/documentos-verificacion/$($docTemp.id)/archivo" $png | Out-Null

$reclamoTemp = Invoke-Api "reclamos.nuevo" "POST" "/api/reclamos/nuevo" @{
    idUsuario = $generador.id; asunto = "Reclamo Smoke $stamp"; descripcion = "Temporal"; evidenciaUrl = "/uploads/mock/reclamo.jpg"; estado = "abierto"
}
Invoke-Api "reclamos.lista" "GET" "/api/reclamos/lista" | Out-Null
Invoke-Api "reclamos.get" "GET" "/api/reclamos/$($reclamoTemp.id)" | Out-Null
Invoke-Api "reclamos.actualiza" "PUT" "/api/reclamos/actualiza" @{
    id = $reclamoTemp.id; idUsuario = $generador.id; asunto = "Reclamo Smoke editado $stamp"; descripcion = "Temporal editado"; evidenciaUrl = "/uploads/mock/reclamo2.jpg"; estado = "respondido"; respuesta = "Respuesta mock"
} | Out-Null
Invoke-Multipart "reclamos.evidencia" "/api/reclamos/$($reclamoTemp.id)/evidencia" $png | Out-Null

$mpTemp = Invoke-Api "metodos-pago.nuevo" "POST" "/api/metodos-pago/nuevo" @{
    idUsuario = $generador.id; tipo = "yape"; alias = "Smoke Pago $stamp"; titular = "Smoke"; detalleEnmascarado = "***999"; principal = $false; activo = $true
}
Invoke-Api "metodos-pago.lista" "GET" "/api/metodos-pago/lista" | Out-Null
Invoke-Api "metodos-pago.usuario" "GET" "/api/metodos-pago/usuario/$($generador.id)" | Out-Null
Invoke-Api "metodos-pago.get" "GET" "/api/metodos-pago/$($mpTemp.id)" | Out-Null
Invoke-Api "metodos-pago.actualiza" "PUT" "/api/metodos-pago/actualiza" @{
    id = $mpTemp.id; idUsuario = $generador.id; tipo = "yape"; alias = "Smoke Pago editado $stamp"; titular = "Smoke"; detalleEnmascarado = "***888"; principal = $false; activo = $true
} | Out-Null
Invoke-Api "metodos-pago.principal" "PATCH" "/api/metodos-pago/$($mpTemp.id)/principal" | Out-Null

$notifTemp = Invoke-Api "notificaciones.enviar" "POST" "/api/notificaciones/enviar" @{
    idUsuario = $generador.id; tipo = "smoke"; titulo = "Notificacion Smoke $stamp"; mensaje = "Temporal"; leida = $false; estado = "pendiente"
}
Invoke-Api "notificaciones.push" "POST" "/api/notificaciones/push" @{
    idUsuario = $generador.id; deviceToken = "device-token-smoke-123456"; titulo = "Push Smoke"; mensaje = "Temporal"; tipo = "push"
} | Out-Null
Invoke-Api "notificaciones.lista" "GET" "/api/notificaciones/lista" | Out-Null
Invoke-Api "notificaciones.usuario" "GET" "/api/notificaciones/usuario/$($generador.id)" | Out-Null
Invoke-Api "notificaciones.cola" "GET" "/api/notificaciones/cola?estado=pendiente" | Out-Null
Invoke-Api "notificaciones.generar-logros" "POST" "/api/notificaciones/generar-logros/$($generador.id)" -Expected @(200, 201, 404) | Out-Null
Invoke-Api "notificaciones.get" "GET" "/api/notificaciones/$($notifTemp.id)" | Out-Null
Invoke-Api "notificaciones.leida" "PATCH" "/api/notificaciones/$($notifTemp.id)/leida?leida=true" | Out-Null
Invoke-Api "notificaciones.estado" "PATCH" "/api/notificaciones/$($notifTemp.id)/estado?estado=enviada" | Out-Null

$faqTemp = Invoke-Api "soporte.faqs.nuevo" "POST" "/api/soporte/faqs/nuevo" @{
    categoria = "smoke"; pregunta = "Pregunta Smoke $stamp"; respuesta = "Respuesta"; activo = $true
}
Invoke-Api "soporte.faqs.lista" "GET" "/api/soporte/faqs/lista" | Out-Null
Invoke-Api "soporte.faqs.buscar" "GET" "/api/soporte/faqs/buscar?q=Smoke" | Out-Null
Invoke-Api "soporte.faqs.get" "GET" "/api/soporte/faqs/$($faqTemp.id)" | Out-Null
Invoke-Api "soporte.faqs.actualiza" "PUT" "/api/soporte/faqs/actualiza" @{
    id = $faqTemp.id; categoria = "smoke"; pregunta = "Pregunta Smoke editada $stamp"; respuesta = "Respuesta editada"; activo = $true
} | Out-Null
$contactTemp = Invoke-Api "soporte.contactos.nuevo" "POST" "/api/soporte/contactos/nuevo" @{
    tipo = "telefono"; valor = "+51 900 $stamp"; descripcion = "Contacto smoke"; horario = "24/7"; activo = $true
}
Invoke-Api "soporte.contacto" "GET" "/api/soporte/contacto" | Out-Null
Invoke-Api "soporte.contactos.lista" "GET" "/api/soporte/contactos/lista" | Out-Null
Invoke-Api "soporte.contactos.get" "GET" "/api/soporte/contactos/$($contactTemp.id)" | Out-Null
Invoke-Api "soporte.contactos.actualiza" "PUT" "/api/soporte/contactos/actualiza" @{
    id = $contactTemp.id; tipo = "telefono"; valor = "+51 901 $stamp"; descripcion = "Contacto smoke editado"; horario = "9-18"; activo = $true
} | Out-Null

$tdTemp = Invoke-Api "transacciones-dinero.nuevo" "POST" "/api/transacciones-dinero/nuevo" @{
    idUsuario = $generador.id; tipo = "pago"; monto = 12.50; moneda = "PEN"; estado = "pendiente"; concepto = "Smoke dinero $stamp"; metodoPagoTipo = "yape"; metodoPagoDetalle = "***777"; referenciaExterna = "SMOKE-$stamp"; referenciaTipo = "test"
}
Invoke-Api "transacciones-dinero.lista" "GET" "/api/transacciones-dinero/lista" | Out-Null
Invoke-Api "transacciones-dinero.get" "GET" "/api/transacciones-dinero/$($tdTemp.id)" | Out-Null
Invoke-Api "transacciones-dinero.actualiza" "PUT" "/api/transacciones-dinero/actualiza" @{
    id = $tdTemp.id; idUsuario = $generador.id; tipo = "pago"; monto = 13.50; moneda = "PEN"; estado = "completada"; concepto = "Smoke dinero editado $stamp"; metodoPagoTipo = "yape"; metodoPagoDetalle = "***777"; referenciaExterna = "SMOKE-$stamp"; referenciaTipo = "test"
} | Out-Null

$tpTemp = Invoke-Api "transacciones-puntos.nuevo" "POST" "/api/transacciones-puntos/nuevo" @{
    idUsuario = $generador.id; tipo = "ganado"; puntos = 9; motivo = "Smoke puntos $stamp"; referenciaTipo = "test"
}
Invoke-Api "transacciones-puntos.lista" "GET" "/api/transacciones-puntos/lista" | Out-Null
Invoke-Api "transacciones-puntos.get" "GET" "/api/transacciones-puntos/$($tpTemp.id)" | Out-Null
Invoke-Api "transacciones-puntos.actualiza" "PUT" "/api/transacciones-puntos/actualiza" @{
    id = $tpTemp.id; idUsuario = $generador.id; tipo = "ganado"; puntos = 11; motivo = "Smoke puntos editado $stamp"; referenciaTipo = "test"
} | Out-Null
Invoke-Api "transacciones-puntos.total-mes" "GET" "/api/transacciones-puntos/total-mes/$($generador.id)?mes=$((Get-Date).ToString('yyyy-MM'))" | Out-Null
Invoke-Api "transacciones-puntos.historial" "GET" "/api/transacciones-puntos/historial/$($generador.id)" | Out-Null

Invoke-Multipart "archivos.imagenes" "/api/archivos/imagenes" $png | Out-Null
Invoke-Api "geolocalizacion.distancia" "GET" "/api/geolocalizacion/distancia?latOrigen=-12.046&lngOrigen=-77.043&latDestino=-12.071&lngDestino=-77.079" | Out-Null
Invoke-Api "geolocalizacion.matriz" "POST" "/api/geolocalizacion/matriz" @{
    origenes = @(@{ id = "a"; nombre = "A"; latitud = -12.046; longitud = -77.043 });
    destinos = @(@{ id = "b"; nombre = "B"; latitud = -12.071; longitud = -77.079 })
} | Out-Null

Invoke-Api "notificaciones.delete" "DELETE" "/api/notificaciones/$($notifTemp.id)" | Out-Null
Invoke-Api "metodos-pago.delete" "DELETE" "/api/metodos-pago/$($mpTemp.id)" | Out-Null
Invoke-Api "reclamos.delete" "DELETE" "/api/reclamos/$($reclamoTemp.id)" | Out-Null
Invoke-Api "documentos.delete" "DELETE" "/api/documentos-verificacion/$($docTemp.id)" | Out-Null
Invoke-Api "certificados.delete" "DELETE" "/api/certificados/$($certTemp.id)" | Out-Null
Invoke-Api "usuarios-incentivos.delete" "DELETE" "/api/usuarios-incentivos/$($uiTemp.id)" | Out-Null
Invoke-Api "incentivos.delete" "DELETE" "/api/incentivos/$($incTemp.id)" | Out-Null
Invoke-Api "rutas.delete" "DELETE" "/api/rutas/$($rutaTemp.id)" | Out-Null
Invoke-Api "mensajes-chat.delete" "DELETE" "/api/mensajes-chat/$($msgTemp.id)" | Out-Null
Invoke-Api "calificaciones.delete" "DELETE" "/api/calificaciones/$($calTemp.id)" | Out-Null
Invoke-Api "recolecciones.cancelar" "PATCH" "/api/recolecciones/$($recTemp.id)/cancelar" | Out-Null
Invoke-Api "recolecciones.delete" "DELETE" "/api/recolecciones/$($recTemp.id)" | Out-Null
Invoke-Api "publicaciones-materiales.delete" "DELETE" "/api/publicaciones-materiales/$($pubTemp.id)/$($matTemp.id)" | Out-Null
Invoke-Api "publicaciones.delete" "DELETE" "/api/publicaciones/$($pubTemp.id)" | Out-Null
Invoke-Api "materiales.delete" "DELETE" "/api/materiales/$($matTemp.id)" | Out-Null
Invoke-Api "soporte.faqs.delete" "DELETE" "/api/soporte/faqs/$($faqTemp.id)" | Out-Null
Invoke-Api "soporte.contactos.delete" "DELETE" "/api/soporte/contactos/$($contactTemp.id)" | Out-Null
Invoke-Api "transacciones-dinero.delete" "DELETE" "/api/transacciones-dinero/$($tdTemp.id)" | Out-Null
Invoke-Api "transacciones-puntos.delete" "DELETE" "/api/transacciones-puntos/$($tpTemp.id)" | Out-Null
Invoke-Api "usuarios.delete" "DELETE" "/api/usuarios/$($tempUser.id)" | Out-Null
Invoke-Api "auth.logout" "POST" "/auth/logout" @{ refreshToken = $refreshToken } | Out-Null

$Results | ConvertTo-Json -Depth 8 | Set-Content -Encoding UTF8 (Join-Path $OutDir "endpoint-smoke-results.json")
$failed = @($Results | Where-Object { -not $_.ok })
$summary = [pscustomobject]@{
    total = $Results.Count
    passed = $Results.Count - $failed.Count
    failed = $failed.Count
    resultsFile = (Join-Path $OutDir "endpoint-smoke-results.json")
}
$summary | ConvertTo-Json -Depth 4 | Set-Content -Encoding UTF8 (Join-Path $OutDir "endpoint-smoke-summary.json")

if ($failed.Count -gt 0) {
    $failed | Format-Table name, method, path, status -AutoSize
    throw "$($failed.Count) endpoint smoke tests failed"
}

$summary | Format-List
