# QhuriNet - User Stories vs Backend

Base URL local: `http://localhost:8083`

Datos mock cargados con perfil `mock-data`:

- `admin / admin123`
- `generador / qhuri123`
- `bodega / qhuri123`
- `recolector / qhuri123`

Flujo base para Postman:

1. `POST /auth/login` con `admin/admin123`.
2. Guardar `accessToken`.
3. Enviar `Authorization: Bearer {{accessToken}}` en los endpoints protegidos.
4. Consultar IDs vivos con `/api/usuarios/lista`, `/api/publicaciones/lista`, `/api/recolecciones/lista`, etc.

| US | Historia | Como ejecutarla en backend |
|---|---|---|
| `01-EP1` | Registrar material reciclable | `POST /api/publicaciones/nuevo` y luego `POST /api/publicaciones-materiales/nuevo`. |
| `02-EP1` | Registrar observaciones del material | Campo `observaciones` en `POST/PUT /api/publicaciones`. |
| `03-EP1` | Clasificar material automáticamente | `POST /api/materiales/clasificar` o `POST /api/publicaciones/{id}/clasificar-materiales`. |
| `04-EP1` | Validar información registrada | Validaciones en `POST /api/publicaciones/nuevo`: usuario, titulo, coordenadas, fecha y observaciones. |
| `05-EP1` | Adjuntar evidencia visual | `POST /api/publicaciones/{id}/evidencia` con `multipart/form-data`; alternativa: `PATCH /api/publicaciones/{id}/evidencia-url`. |
| `06-EP1` | Seleccionar fecha de disponibilidad | Campo `fechaDisponibilidad` en `POST/PUT /api/publicaciones`. |
| `14-EP1` | Chatear con recolector asignado | CRUD de `MensajeChat`: `/api/mensajes-chat/*`. |
| `07-EP2` | Confirmar entrega mediante QR | `GET /api/recolecciones/{id}/qr` y `POST /api/recolecciones/{id}/validar-qr`. |
| `08-EP2` | Reprogramar recolección | `PATCH /api/recolecciones/{id}/reprogramar`. |
| `09-EP2` | Cancelar recolección | `PATCH /api/recolecciones/{id}/cancelar`. |
| `10-EP2` | Perfil y reputación del recolector | `GET /api/usuarios/{idRecolector}/perfil-recolector` y `GET /api/recolecciones/promedio-recolector/{idRecolector}`. |
| `11-EP2` | Calificar servicio de recolección | CRUD de `/api/calificaciones/*`. |
| `12-EP2` | Reportar incidencia en recolección | `PATCH /api/recolecciones/{id}/incidencia` y `GET /api/recolecciones/incidencias/usuario/{idUsuario}`. |
| `13-EP2` | Marcar recolección prioritaria | `PATCH /api/recolecciones/{id}/prioridad?prioritaria=true`. |
| `15-EP3` | Acumular puntos por reciclaje | `POST /api/recolecciones/{id}/validar-qr` acredita puntos; consultar con `GET /api/usuarios/{id}/puntos`. |
| `16-EP3` | Canjear puntos por recompensas | `POST /api/usuarios-incentivos/canjear`. |
| `17-EP3` | Recompensas diarias y desafíos | CRUD de `/api/incentivos/*` y `GET /api/incentivos/disponibles/{idUsuario}`. |
| `18-EP3` | Tienda de puntos | `GET /api/incentivos/disponibles/{idUsuario}`. |
| `19-EP3` | Recordatorios y estados de incentivos | `GET /api/usuarios-incentivos/recordatorios/{idUsuario}`. |
| `20-EP3` | Certificados digitales | CRUD de `/api/certificados/*`. |
| `21-EP3` | Historial de incentivos | `GET /api/usuarios-incentivos/progreso/{idUsuario}` y `GET /api/transacciones-puntos/historial/{idUsuario}`. |
| `22-EP3` | Notificaciones de logros y recompensas | `POST /api/notificaciones/generar-logros/{idUsuario}` y `GET /api/notificaciones/usuario/{idUsuario}`. |
| `23-EP3` | Niveles de participación | `GET /api/usuarios/{idUsuario}/estadisticas` y campos `puntosTotales/nivelParticipacion`. |
| `24-EP3` | Detalle del valor de puntos por acción | `GET /api/materiales/lista`, `GET /api/usuarios/{idUsuario}/puntos`. |
| `25-EP3` | Filtrar certificados por dificultad | `GET /api/certificados/por-dificultad?nivel=facil`. |
| `26-EP4` | Visualizar mapa de recolección | `GET /api/publicaciones/mapa?lat=-12.046&lng=-77.043&radio_km=20`. |
| `27-EP4` | Filtros por material y tipo de punto | `GET /api/publicaciones/mapa?...&material=Plastico&categoria=plastico&tipo_punto=GENERADOR`. |
| `28-EP4` | Buscador de puntos de recolección | `GET /api/publicaciones/buscar?q=Botellas`. |
| `29-EP4` | Lista de puntos de reciclaje | `GET /api/publicaciones/lista` o `GET /api/publicaciones/cercanas?...`. |
| `30-EP4` | Detalle del punto seleccionado | `GET /api/publicaciones/{id}`. |
| `31-EP4` | Generar ruta óptima entre puntos | `POST /api/rutas/optima` con puntos y, opcionalmente, aristas. |
| `32-EP4` | Visualizar detalles de la ruta | `GET /api/rutas/{id}`. |
| `33-EP4` | Guardar y reutilizar rutas personalizadas | CRUD de `/api/rutas/*`, `GET /api/rutas/usuario/{idUsuario}`, `PATCH /api/rutas/{id}/favorita`. |
| `34-EP4` | Seguimiento del recolector en tiempo real | `PATCH /api/recolecciones/{id}/ubicacion-recolector`, `GET /api/recolecciones/{id}/ubicacion-recolector`, `GET /api/recolecciones/{id}/ubicacion-recolector/stream`. |
| `35-EP5` | Registro con correo, Google o Facebook | `POST /auth/registro` y `POST /auth/social-login`. |
| `36-EP5` | Completar datos básicos del perfil | `PUT /api/usuarios/actualiza`. |
| `37-EP5` | Subir documentación de verificación | `POST /api/documentos-verificacion/nuevo` y `POST /api/documentos-verificacion/{id}/archivo`. |
| `38-EP5` | Disponibilidad y visibilidad del perfil | Campos `disponible/verificado` en `PUT /api/usuarios/actualiza`. |
| `39-EP5` | Vincular método de pago | CRUD de `/api/metodos-pago/*`. |
| `40-EP5` | Establecer foto de perfil | `POST /api/usuarios/{id}/foto`; alternativa: `PATCH /api/usuarios/{id}/foto-url`. |
| `41-EP5` | Seleccionar tipo de cuenta | Campo `tipoCuenta` en `POST /auth/registro` o `PUT /api/usuarios/actualiza`. |
| `42-EP5` | Descripción del perfil | Campo `descripcion` en `PUT /api/usuarios/actualiza`. |
| `43-EP6` | Historial de materiales reciclados | `GET /api/publicaciones/usuario/{idUsuario}/historial-materiales`. |
| `44-EP6` | Reporte de actividades del perfil | `GET /api/usuarios/{idUsuario}/estadisticas`. |
| `45-EP6` | Historial de actividades | `GET /api/recolecciones/historial/{idUsuario}`. |
| `46-EP6` | Descargar comprobantes PDF/CSV | `GET /api/recolecciones/{id}/comprobante.pdf`, `GET /api/recolecciones/{id}/comprobante.csv`, `GET /api/recolecciones/historial/{idUsuario}/comprobante.csv`. |
| `47-EP6` | Historial de actividades con detalle | `GET /api/recolecciones/actividades-detalle?idUsuario={idUsuario}`. |
| `48-EP7` | Centro de soporte FAQ y contacto | `/api/soporte/faqs/*`, `/api/soporte/contacto`, `/api/soporte/contactos/*`. |
| `49-EP7` | Reclamos con evidencia | CRUD de `/api/reclamos/*` y `POST /api/reclamos/{id}/evidencia`. |
| `50-EP7` | Contacto telefónico con soporte | `GET /api/soporte/contacto`. |
| `51-EP8` | Autenticar usuario vía API | `/auth/login`, `/auth/registro`, `/auth/refresh`, `/auth/logout`. |
| `52-EP8` | Gestionar publicaciones vía API | CRUD/query de `/api/publicaciones/*`. |
| `53-EP8` | Gestionar recolecciones vía API | CRUD/query de `/api/recolecciones/*`. |
| `54-EP8` | Calcular ruta óptima vía API | `POST /api/rutas/optima`. |
| `55-EP8` | Integrar geolocalización vía API | `GET /api/geolocalizacion/distancia` y `POST /api/geolocalizacion/matriz`. |
| `56-EP8` | Gestionar puntos vía API | `/api/transacciones-puntos/*` y `GET /api/usuarios/{idUsuario}/puntos`. |
| `57-EP8` | Subir imágenes vía API | `POST /api/archivos/imagenes` y endpoints específicos de foto/evidencia/archivo. |
| `58-EP8` | Enviar notificaciones push vía API | `POST /api/notificaciones/push` mock y `/api/notificaciones/*`. |
| `59-EP8` | Generar y validar QR vía API | `GET /api/recolecciones/{id}/qr` y `POST /api/recolecciones/{id}/validar-qr`. |
| `60-EP8` | Obtener estadísticas vía API | `GET /api/usuarios/{idUsuario}/estadisticas`, `GET /api/usuarios/ranking`, `GET /api/usuarios/{idUsuario}/estadisticas/kg-por-mes`. |
| `51b-EP9` | JWT con refresh token | `POST /auth/login`, `POST /auth/refresh`. |
| `52b-EP9` | Restringir endpoints por rol | Probar endpoints admin-only como `GET /api/usuarios/lista` con token admin vs token usuario. |
| `53b-EP9` | BCrypt para contraseñas | `POST /auth/registro`; la contraseña se guarda hasheada y se valida con `/auth/login`. |
| `54b-EP9` | Permisos a nivel de recurso | Probar endpoints de dueño: publicaciones, rutas, métodos de pago, documentos, notificaciones y recolecciones. |
| `55b-EP9` | Rate limiting en endpoints críticos | Repetir login inválido más de 5 veces por minuto en `/auth/login`; devuelve `429`. |
| `56b-EP9` | Logout con invalidación de refresh token | `POST /auth/logout` con `refreshToken`; luego probar `/auth/refresh` con el mismo token. |

Smoke test ejecutado:

```powershell
.\scripts\smoke-test-endpoints.ps1 -BaseUrl http://localhost:8083
```

Resultado esperado: `164` pruebas pasadas, `0` fallidas. El detalle queda en `target/smoke-test/endpoint-smoke-results.json`.
