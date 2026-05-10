package pe.edu.upc.qhurinet.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upc.qhurinet.entities.Calificacion;
import pe.edu.upc.qhurinet.entities.Certificado;
import pe.edu.upc.qhurinet.entities.DocumentoVerificacion;
import pe.edu.upc.qhurinet.entities.Faq;
import pe.edu.upc.qhurinet.entities.Incentivo;
import pe.edu.upc.qhurinet.entities.Material;
import pe.edu.upc.qhurinet.entities.MensajeChat;
import pe.edu.upc.qhurinet.entities.MetodoPago;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.entities.Publicacion;
import pe.edu.upc.qhurinet.entities.PublicacionMaterial;
import pe.edu.upc.qhurinet.entities.PublicacionMaterialId;
import pe.edu.upc.qhurinet.entities.Reclamo;
import pe.edu.upc.qhurinet.entities.Recoleccion;
import pe.edu.upc.qhurinet.entities.Ruta;
import pe.edu.upc.qhurinet.entities.SoporteContacto;
import pe.edu.upc.qhurinet.entities.TransaccionDinero;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.UbicacionRecolectorHistorial;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.entities.UsuarioIncentivo;
import pe.edu.upc.qhurinet.repositories.ICalificacionRepository;
import pe.edu.upc.qhurinet.repositories.ICertificadoRepository;
import pe.edu.upc.qhurinet.repositories.IDocumentoVerificacionRepository;
import pe.edu.upc.qhurinet.repositories.IFaqRepository;
import pe.edu.upc.qhurinet.repositories.IIncentivoRepository;
import pe.edu.upc.qhurinet.repositories.IMaterialRepository;
import pe.edu.upc.qhurinet.repositories.IMensajeChatRepository;
import pe.edu.upc.qhurinet.repositories.IMetodoPagoRepository;
import pe.edu.upc.qhurinet.repositories.INotificacionRepository;
import pe.edu.upc.qhurinet.repositories.IPublicacionMaterialRepository;
import pe.edu.upc.qhurinet.repositories.IPublicacionRepository;
import pe.edu.upc.qhurinet.repositories.IReclamoRepository;
import pe.edu.upc.qhurinet.repositories.IRecoleccionRepository;
import pe.edu.upc.qhurinet.repositories.IRutaRepository;
import pe.edu.upc.qhurinet.repositories.ISoporteContactoRepository;
import pe.edu.upc.qhurinet.repositories.ITransaccionDineroRepository;
import pe.edu.upc.qhurinet.repositories.ITransaccionPuntosRepository;
import pe.edu.upc.qhurinet.repositories.IUbicacionRecolectorHistorialRepository;
import pe.edu.upc.qhurinet.repositories.IUsuarioIncentivoRepository;
import pe.edu.upc.qhurinet.repositories.IUsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mock-data")
public class MockDataSeeder implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final IUsuarioRepository usuarioRepository;
    private final IMaterialRepository materialRepository;
    private final IPublicacionRepository publicacionRepository;
    private final IPublicacionMaterialRepository publicacionMaterialRepository;
    private final IRecoleccionRepository recoleccionRepository;
    private final ICalificacionRepository calificacionRepository;
    private final IMensajeChatRepository mensajeChatRepository;
    private final IIncentivoRepository incentivoRepository;
    private final IUsuarioIncentivoRepository usuarioIncentivoRepository;
    private final ICertificadoRepository certificadoRepository;
    private final IDocumentoVerificacionRepository documentoRepository;
    private final IReclamoRepository reclamoRepository;
    private final ITransaccionPuntosRepository transaccionPuntosRepository;
    private final ITransaccionDineroRepository transaccionDineroRepository;
    private final IRutaRepository rutaRepository;
    private final IMetodoPagoRepository metodoPagoRepository;
    private final INotificacionRepository notificacionRepository;
    private final IFaqRepository faqRepository;
    private final ISoporteContactoRepository soporteContactoRepository;
    private final IUbicacionRecolectorHistorialRepository ubicacionRepository;

    public MockDataSeeder(PasswordEncoder passwordEncoder,
                          IUsuarioRepository usuarioRepository,
                          IMaterialRepository materialRepository,
                          IPublicacionRepository publicacionRepository,
                          IPublicacionMaterialRepository publicacionMaterialRepository,
                          IRecoleccionRepository recoleccionRepository,
                          ICalificacionRepository calificacionRepository,
                          IMensajeChatRepository mensajeChatRepository,
                          IIncentivoRepository incentivoRepository,
                          IUsuarioIncentivoRepository usuarioIncentivoRepository,
                          ICertificadoRepository certificadoRepository,
                          IDocumentoVerificacionRepository documentoRepository,
                          IReclamoRepository reclamoRepository,
                          ITransaccionPuntosRepository transaccionPuntosRepository,
                          ITransaccionDineroRepository transaccionDineroRepository,
                          IRutaRepository rutaRepository,
                          IMetodoPagoRepository metodoPagoRepository,
                          INotificacionRepository notificacionRepository,
                          IFaqRepository faqRepository,
                          ISoporteContactoRepository soporteContactoRepository,
                          IUbicacionRecolectorHistorialRepository ubicacionRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.materialRepository = materialRepository;
        this.publicacionRepository = publicacionRepository;
        this.publicacionMaterialRepository = publicacionMaterialRepository;
        this.recoleccionRepository = recoleccionRepository;
        this.calificacionRepository = calificacionRepository;
        this.mensajeChatRepository = mensajeChatRepository;
        this.incentivoRepository = incentivoRepository;
        this.usuarioIncentivoRepository = usuarioIncentivoRepository;
        this.certificadoRepository = certificadoRepository;
        this.documentoRepository = documentoRepository;
        this.reclamoRepository = reclamoRepository;
        this.transaccionPuntosRepository = transaccionPuntosRepository;
        this.transaccionDineroRepository = transaccionDineroRepository;
        this.rutaRepository = rutaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.notificacionRepository = notificacionRepository;
        this.faqRepository = faqRepository;
        this.soporteContactoRepository = soporteContactoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Usuario admin = usuario("admin", "admin@qhurinet.test", "Admin QhuriNet", "ADMIN", List.of("ADMIN", "GENERADOR", "RECOLECTOR"), 2000);
        Usuario generador = usuario("generador", "generador@qhurinet.test", "Lucia Generadora", "GENERADOR", List.of("GENERADOR"), 820);
        Usuario bodega = usuario("bodega", "bodega@qhurinet.test", "Bodega Verde", "BODEGA", List.of("GENERADOR"), 530);
        Usuario recolector = usuario("recolector", "recolector@qhurinet.test", "Nicolas Recolector", "RECOLECTOR", List.of("RECOLECTOR"), 340);

        Material plastico = material("Plastico PET", "plastico", "Botellas y envases PET", "2.50");
        Material carton = material("Carton", "carton", "Cajas y empaques limpios", "1.80");
        Material vidrio = material("Vidrio", "vidrio", "Botellas y frascos de vidrio", "2.00");
        Material metal = material("Aluminio", "metal", "Latas y piezas metalicas", "3.00");
        Material papel = material("Papel", "papel", "Hojas, periodicos y revistas", "1.20");

        Publicacion pubBotellas = publicacion(generador, "Botellas PET limpias para reciclar",
                "Tengo botellas plasticas PET y tapas separadas", "activa",
                "-12.046374", "-77.042793", "Centro de Lima", LocalDate.now().plusDays(2), "/uploads/mock/botellas.jpg");
        Publicacion pubCarton = publicacion(bodega, "Cajas de carton de bodega",
                "Carton limpio y compactado, disponible en la tarde", "activa",
                "-12.071900", "-77.079200", "Av. Brasil 123", LocalDate.now().plusDays(3), "/uploads/mock/carton.jpg");
        Publicacion pubVidrio = publicacion(generador, "Frascos de vidrio y latas",
                "Vidrio transparente y algunas latas de aluminio", "recolectada",
                "-12.093100", "-77.046500", "Miraflores", LocalDate.now().minusDays(1), "/uploads/mock/vidrio.jpg");

        publicacionMaterial(pubBotellas, plastico, "12.50", "kg");
        publicacionMaterial(pubCarton, carton, "18.00", "kg");
        publicacionMaterial(pubVidrio, vidrio, "7.00", "kg");
        publicacionMaterial(pubVidrio, metal, "2.00", "kg");
        publicacionMaterial(pubBotellas, papel, "1.50", "kg");

        Recoleccion recProgramada = recoleccion(pubBotellas, recolector, "programada", LocalDateTime.now().plusDays(1), null, true, "-12.050000", "-77.040000", null);
        Recoleccion recCompletada = recoleccion(pubVidrio, recolector, "completada", LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), false, "-12.090000", "-77.045000", "QR-MOCK-VALIDADO");

        calificacion(recCompletada, generador, 5, "Recojo puntual y ordenado");
        mensaje(recProgramada, generador, "Hola, puedo entregar las botellas desde las 4 pm.");
        mensaje(recProgramada, recolector, "Perfecto, paso por la direccion indicada.");
        ubicacion(recProgramada, "-12.050100", "-77.040100");

        Incentivo desafio = incentivo("desafio", "Recicla 10 kg esta semana", "Completa 10 kg reciclados para obtener puntos extra", 0, 10, "kg", 50);
        Incentivo recompensa = incentivo("recompensa", "Vale de descuento eco", "Canje simple para tiendas aliadas", 300, null, null, 20);
        usuarioIncentivo(generador, desafio, 8, "en_progreso", null);
        usuarioIncentivo(generador, recompensa, 1, "completado", LocalDateTime.now().minusDays(1));

        certificado(generador, "Reciclador Bronce", "Certificado por primeras recolecciones completadas", "facil", 100);
        documento(generador, "dni", "/uploads/mock/dni-generador.pdf", "aprobado");
        reclamo(generador, "Consulta sobre puntos", "No veo reflejado un canje de puntos", "/uploads/mock/reclamo.jpg", "abierto");
        transaccionPuntos(generador, "ganado", 120, "Carga mock inicial", "seed", pubBotellas.getId());
        transaccionDinero(bodega, "pago", "15.00", "PEN", "completada", "Servicio de recoleccion prioritaria", "yape", "***123");
        ruta(recolector, "Ruta Centro - Miraflores", "[{\"id\":\"centro\",\"latitud\":-12.046374,\"longitud\":-77.042793},{\"id\":\"miraflores\",\"latitud\":-12.093100,\"longitud\":-77.046500}]", "5.20", 16, true);
        metodoPago(generador, "yape", "Yape personal", "Lucia Generadora", "*** *** 321", true);
        notificacion(generador, "logro", "Logro mock", "Alcanzaste una meta de reciclaje.", "pendiente");
        faq("publicaciones", "Como publico materiales reciclables?", "Registra una publicacion con material, cantidad y disponibilidad.");
        soporte("telefono", "+51 999 888 777", "Soporte telefonico mock", "Lunes a viernes 9:00-18:00");

        System.out.println("QHURINET_MOCK_DATA_READY");
        System.out.println("USUARIOS: admin/admin123, generador/qhuri123, bodega/qhuri123, recolector/qhuri123");
        System.out.println("IDS: admin=" + admin.getId() + ", generador=" + generador.getId() + ", bodega=" + bodega.getId() + ", recolector=" + recolector.getId());
        System.out.println("IDS: publicacion=" + pubBotellas.getId() + ", recoleccion=" + recProgramada.getId() + ", recoleccionCompletada=" + recCompletada.getId());
    }

    private Usuario usuario(String username, String email, String nombre, String tipoCuenta, List<String> roles, int puntos) {
        Usuario usuario = Optional.ofNullable(usuarioRepository.findOneByUsername(username)).orElseGet(Usuario::new);
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setNombre(nombre);
        usuario.setPasswordHash(passwordEncoder.encode(username.equals("admin") ? "admin123" : "qhuri123"));
        usuario.setTelefono("999888777");
        usuario.setFotoUrl("/uploads/mock/" + username + ".jpg");
        usuario.setDescripcion("Usuario mock para pruebas de Postman");
        usuario.setTipoCuenta(tipoCuenta);
        usuario.setProveedorAuth("local");
        usuario.setDisponible(true);
        usuario.setVerificado(true);
        usuario.setPuntosTotales(puntos);
        usuario.setNivelParticipacion(puntos >= 800 ? "Oro" : "Bronce");
        usuario.syncRoles(roles);
        return usuarioRepository.save(usuario);
    }

    private Material material(String nombre, String categoria, String descripcion, String puntosPorKg) {
        Material material = materialRepository.findAll().stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(Material::new);
        material.setNombre(nombre);
        material.setCategoria(categoria);
        material.setDescripcion(descripcion);
        material.setPuntosPorKg(new BigDecimal(puntosPorKg));
        return materialRepository.save(material);
    }

    private Publicacion publicacion(Usuario usuario, String titulo, String observaciones, String estado,
                                   String lat, String lng, String direccion, LocalDate fecha, String imagen) {
        Publicacion publicacion = publicacionRepository.findAll().stream()
                .filter(p -> p.getTitulo().equalsIgnoreCase(titulo))
                .findFirst()
                .orElseGet(Publicacion::new);
        publicacion.setUsuario(usuario);
        publicacion.setTitulo(titulo);
        publicacion.setObservaciones(observaciones);
        publicacion.setEstado(estado);
        publicacion.setLatitud(new BigDecimal(lat));
        publicacion.setLongitud(new BigDecimal(lng));
        publicacion.setDireccionReferencia(direccion);
        publicacion.setFechaDisponibilidad(fecha);
        publicacion.setImagenesJson(imagen);
        return publicacionRepository.save(publicacion);
    }

    private void publicacionMaterial(Publicacion publicacion, Material material, String cantidad, String unidad) {
        PublicacionMaterialId id = new PublicacionMaterialId(publicacion.getId(), material.getId());
        PublicacionMaterial pm = publicacionMaterialRepository.findById(id).orElseGet(PublicacionMaterial::new);
        pm.setId(id);
        pm.setPublicacion(publicacion);
        pm.setMaterial(material);
        pm.setCantidad(new BigDecimal(cantidad));
        pm.setUnidad(unidad);
        publicacionMaterialRepository.save(pm);
    }

    private Recoleccion recoleccion(Publicacion publicacion, Usuario recolector, String estado, LocalDateTime programada,
                                    LocalDateTime completada, boolean prioritaria, String lat, String lng, String qr) {
        Recoleccion recoleccion = recoleccionRepository.findAll().stream()
                .filter(r -> r.getPublicacion().getId().equals(publicacion.getId()))
                .findFirst()
                .orElseGet(Recoleccion::new);
        recoleccion.setPublicacion(publicacion);
        recoleccion.setRecolector(recolector);
        recoleccion.setEstado(estado);
        recoleccion.setFechaProgramada(programada);
        recoleccion.setFechaCompletada(completada);
        recoleccion.setPrioritaria(prioritaria);
        recoleccion.setCodigoQr(qr == null ? "QR-MOCK-" + publicacion.getId() : qr);
        recoleccion.setQrValidado(completada != null);
        recoleccion.setLatRecolector(new BigDecimal(lat));
        recoleccion.setLngRecolector(new BigDecimal(lng));
        return recoleccionRepository.save(recoleccion);
    }

    private void calificacion(Recoleccion recoleccion, Usuario autor, int puntuacion, String comentario) {
        if (calificacionRepository.findAll().stream().anyMatch(c -> c.getRecoleccion().getId().equals(recoleccion.getId()))) {
            return;
        }
        Calificacion c = new Calificacion();
        c.setRecoleccion(recoleccion);
        c.setAutor(autor);
        c.setPuntuacion(puntuacion);
        c.setComentario(comentario);
        calificacionRepository.save(c);
    }

    private void mensaje(Recoleccion recoleccion, Usuario remitente, String contenido) {
        if (mensajeChatRepository.findAll().stream().anyMatch(m -> contenido.equals(m.getContenido()))) {
            return;
        }
        MensajeChat mensaje = new MensajeChat();
        mensaje.setRecoleccion(recoleccion);
        mensaje.setRemitente(remitente);
        mensaje.setContenido(contenido);
        mensaje.setLeido(false);
        mensajeChatRepository.save(mensaje);
    }

    private void ubicacion(Recoleccion recoleccion, String lat, String lng) {
        UbicacionRecolectorHistorial ubicacion = new UbicacionRecolectorHistorial();
        ubicacion.setRecoleccion(recoleccion);
        ubicacion.setLatRecolector(new BigDecimal(lat));
        ubicacion.setLngRecolector(new BigDecimal(lng));
        ubicacionRepository.save(ubicacion);
    }

    private Incentivo incentivo(String tipo, String nombre, String descripcion, int costo, Integer metaCantidad, String metaUnidad, int stock) {
        Incentivo incentivo = incentivoRepository.findAll().stream()
                .filter(i -> i.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(Incentivo::new);
        incentivo.setTipo(tipo);
        incentivo.setNombre(nombre);
        incentivo.setDescripcion(descripcion);
        incentivo.setCostoPuntos(costo);
        incentivo.setMetaCantidad(metaCantidad);
        incentivo.setMetaUnidad(metaUnidad);
        incentivo.setFechaInicio(LocalDate.now().minusDays(3));
        incentivo.setFechaFin(LocalDate.now().plusDays(15));
        incentivo.setStock(stock);
        incentivo.setActivo(true);
        return incentivoRepository.save(incentivo);
    }

    private void usuarioIncentivo(Usuario usuario, Incentivo incentivo, int cantidad, String estado, LocalDateTime completado) {
        UsuarioIncentivo ui = usuarioIncentivoRepository.findAll().stream()
                .filter(x -> x.getUsuario().getId().equals(usuario.getId()) && x.getIncentivo().getId().equals(incentivo.getId()))
                .findFirst()
                .orElseGet(UsuarioIncentivo::new);
        ui.setUsuario(usuario);
        ui.setIncentivo(incentivo);
        ui.setCantidadActual(cantidad);
        ui.setEstado(estado);
        ui.setCompletadoEn(completado);
        usuarioIncentivoRepository.save(ui);
    }

    private void certificado(Usuario usuario, String nombre, String descripcion, String dificultad, int puntos) {
        Certificado certificado = certificadoRepository.findAll().stream()
                .filter(c -> c.getUsuario().getId().equals(usuario.getId()) && c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(Certificado::new);
        certificado.setUsuario(usuario);
        certificado.setNombre(nombre);
        certificado.setDescripcion(descripcion);
        certificado.setNivelDificultad(dificultad);
        certificado.setPuntosRequeridos(puntos);
        certificado.setUrlPdf("/uploads/mock/certificado-bronce.pdf");
        certificadoRepository.save(certificado);
    }

    private void documento(Usuario usuario, String tipo, String url, String estado) {
        DocumentoVerificacion documento = documentoRepository.findAll().stream()
                .filter(d -> d.getUsuario().getId().equals(usuario.getId()) && d.getTipo().equalsIgnoreCase(tipo))
                .findFirst()
                .orElseGet(DocumentoVerificacion::new);
        documento.setUsuario(usuario);
        documento.setTipo(tipo);
        documento.setUrlArchivo(url);
        documento.setEstado(estado);
        documentoRepository.save(documento);
    }

    private void reclamo(Usuario usuario, String asunto, String descripcion, String evidencia, String estado) {
        Reclamo reclamo = reclamoRepository.findAll().stream()
                .filter(r -> r.getUsuario().getId().equals(usuario.getId()) && r.getAsunto().equalsIgnoreCase(asunto))
                .findFirst()
                .orElseGet(Reclamo::new);
        reclamo.setUsuario(usuario);
        reclamo.setAsunto(asunto);
        reclamo.setDescripcion(descripcion);
        reclamo.setEvidenciaUrl(evidencia);
        reclamo.setEstado(estado);
        reclamoRepository.save(reclamo);
    }

    private void transaccionPuntos(Usuario usuario, String tipo, int puntos, String motivo, String referenciaTipo, java.util.UUID referenciaId) {
        if (transaccionPuntosRepository.findAll().stream().anyMatch(t -> motivo.equals(t.getMotivo()))) {
            return;
        }
        TransaccionPuntos tx = new TransaccionPuntos();
        tx.setUsuario(usuario);
        tx.setTipo(tipo);
        tx.setPuntos(puntos);
        tx.setMotivo(motivo);
        tx.setReferenciaTipo(referenciaTipo);
        tx.setReferenciaId(referenciaId);
        transaccionPuntosRepository.save(tx);
    }

    private void transaccionDinero(Usuario usuario, String tipo, String monto, String moneda, String estado, String concepto, String metodoTipo, String detalle) {
        if (transaccionDineroRepository.findAll().stream().anyMatch(t -> concepto.equals(t.getConcepto()))) {
            return;
        }
        TransaccionDinero tx = new TransaccionDinero();
        tx.setUsuario(usuario);
        tx.setTipo(tipo);
        tx.setMonto(new BigDecimal(monto));
        tx.setMoneda(moneda);
        tx.setEstado(estado);
        tx.setConcepto(concepto);
        tx.setMetodoPagoTipo(metodoTipo);
        tx.setMetodoPagoDetalle(detalle);
        tx.setReferenciaExterna("MOCK-001");
        transaccionDineroRepository.save(tx);
    }

    private void ruta(Usuario usuario, String nombre, String puntosJson, String distancia, int tiempo, boolean favorita) {
        Ruta ruta = rutaRepository.findAll().stream()
                .filter(r -> r.getUsuario().getId().equals(usuario.getId()) && r.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseGet(Ruta::new);
        ruta.setUsuario(usuario);
        ruta.setNombre(nombre);
        ruta.setPuntosJson(puntosJson);
        ruta.setDistanciaTotalKm(new BigDecimal(distancia));
        ruta.setTiempoEstimadoMin(tiempo);
        ruta.setFavorita(favorita);
        rutaRepository.save(ruta);
    }

    private void metodoPago(Usuario usuario, String tipo, String alias, String titular, String detalle, boolean principal) {
        MetodoPago metodo = metodoPagoRepository.findAll().stream()
                .filter(m -> m.getUsuario().getId().equals(usuario.getId()) && alias.equals(m.getAlias()))
                .findFirst()
                .orElseGet(MetodoPago::new);
        metodo.setUsuario(usuario);
        metodo.setTipo(tipo);
        metodo.setAlias(alias);
        metodo.setTitular(titular);
        metodo.setDetalleEnmascarado(detalle);
        metodo.setPrincipal(principal);
        metodo.setActivo(true);
        metodoPagoRepository.save(metodo);
    }

    private void notificacion(Usuario usuario, String tipo, String titulo, String mensaje, String estado) {
        if (notificacionRepository.findAll().stream().anyMatch(n -> n.getUsuario().getId().equals(usuario.getId()) && n.getTitulo().equalsIgnoreCase(titulo))) {
            return;
        }
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setEstado(estado);
        notificacionRepository.save(notificacion);
    }

    private void faq(String categoria, String pregunta, String respuesta) {
        Faq faq = faqRepository.findAll().stream()
                .filter(f -> f.getPregunta().equalsIgnoreCase(pregunta))
                .findFirst()
                .orElseGet(Faq::new);
        faq.setCategoria(categoria);
        faq.setPregunta(pregunta);
        faq.setRespuesta(respuesta);
        faq.setActivo(true);
        faqRepository.save(faq);
    }

    private void soporte(String tipo, String valor, String descripcion, String horario) {
        SoporteContacto soporte = soporteContactoRepository.findAll().stream()
                .filter(s -> s.getTipo().equalsIgnoreCase(tipo) && s.getValor().equalsIgnoreCase(valor))
                .findFirst()
                .orElseGet(SoporteContacto::new);
        soporte.setTipo(tipo);
        soporte.setValor(valor);
        soporte.setDescripcion(descripcion);
        soporte.setHorario(horario);
        soporte.setActivo(true);
        soporteContactoRepository.save(soporte);
    }
}
