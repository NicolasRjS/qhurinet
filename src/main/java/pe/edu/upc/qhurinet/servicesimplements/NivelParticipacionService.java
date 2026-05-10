package pe.edu.upc.qhurinet.servicesimplements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upc.qhurinet.entities.Notificacion;
import pe.edu.upc.qhurinet.entities.TransaccionPuntos;
import pe.edu.upc.qhurinet.entities.Usuario;
import pe.edu.upc.qhurinet.servicesinterfaces.INotificacionService;
import pe.edu.upc.qhurinet.servicesinterfaces.ITransaccionPuntosService;
import pe.edu.upc.qhurinet.servicesinterfaces.IUsuarioService;

@Service
public class NivelParticipacionService {
    private static final String REFERENCIA_ASCENSO = "ascenso_nivel";
    private static final int BONUS_ASCENSO = 10;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ITransaccionPuntosService transaccionPuntosService;

    @Autowired
    private INotificacionService notificacionService;

    public boolean actualizarNivelSiCorresponde(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            return false;
        }

        String nivelActual = normalizarNivel(usuario.getNivelParticipacion());
        String nuevoNivel = nivelParaPuntos(usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales());
        if (ranking(nuevoNivel) <= ranking(nivelActual)) {
            if (!nuevoNivel.equals(usuario.getNivelParticipacion())) {
                usuario.setNivelParticipacion(nuevoNivel);
                usuarioService.update(usuario);
            }
            return false;
        }

        String motivo = "Ascenso a " + nuevoNivel;
        usuario.setNivelParticipacion(nuevoNivel);
        if (!transaccionPuntosService.existsByUsuarioAndReferenciaTipoAndMotivo(usuario.getId(), REFERENCIA_ASCENSO, motivo)) {
            usuario.setPuntosTotales((usuario.getPuntosTotales() == null ? 0 : usuario.getPuntosTotales()) + BONUS_ASCENSO);

            TransaccionPuntos bonus = new TransaccionPuntos();
            bonus.setUsuario(usuario);
            bonus.setTipo("bonus");
            bonus.setPuntos(BONUS_ASCENSO);
            bonus.setMotivo(motivo);
            bonus.setReferenciaTipo(REFERENCIA_ASCENSO);
            bonus.setReferenciaId(usuario.getId());
            transaccionPuntosService.insert(bonus);

            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(usuario);
            notificacion.setTipo("logro");
            notificacion.setTitulo("Ascenso de nivel");
            notificacion.setMensaje("Subiste a " + nuevoNivel + " y ganaste " + BONUS_ASCENSO + " puntos bonus.");
            notificacion.setLeida(false);
            notificacion.setEstado("pendiente");
            notificacionService.insert(notificacion);
        }

        usuarioService.update(usuario);
        return true;
    }

    private String nivelParaPuntos(int puntos) {
        if (puntos >= 1000) {
            return "Platino";
        }
        if (puntos >= 600) {
            return "Oro";
        }
        if (puntos >= 250) {
            return "Plata";
        }
        return "Bronce";
    }

    private String normalizarNivel(String nivel) {
        if (nivel == null || nivel.isBlank()) {
            return "Bronce";
        }
        String value = nivel.trim().toLowerCase();
        return switch (value) {
            case "plata" -> "Plata";
            case "oro" -> "Oro";
            case "platino" -> "Platino";
            default -> "Bronce";
        };
    }

    private int ranking(String nivel) {
        return switch (normalizarNivel(nivel)) {
            case "Plata" -> 1;
            case "Oro" -> 2;
            case "Platino" -> 3;
            default -> 0;
        };
    }
}
