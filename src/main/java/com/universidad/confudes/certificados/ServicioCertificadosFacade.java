package com.universidad.confudes.certificados;

import org.springframework.stereotype.Service;

@Service
public class ServicioCertificadosFacade implements ServicioCertificados {

    private final ValidadorAsistencia validador;
    private final GeneradorCertificadoPDF generador;
    private final FirmaDigitalService firma;
    private final EnvioCorreoService correo;

    public ServicioCertificadosFacade(ValidadorAsistencia validador,
                                      GeneradorCertificadoPDF generador,
                                      FirmaDigitalService firma,
                                      EnvioCorreoService correo) {
        this.validador = validador;
        this.generador = generador;
        this.firma = firma;
        this.correo = correo;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        if (!validador.tieneAsistenciaMinima(solicitud.getParticipanteId(), solicitud.getEventoId(), 0.8)) {
            throw new IllegalStateException("Asistencia insuficiente");
        }

        byte[] doc = generador.iniciarDocumento("plantilla-2026");
        generador.insertarDatosParticipante(doc, solicitud.getNombre(), solicitud.getEventoId(), "2026-08-06");
        byte[] documentoFinal = generador.finalizarDocumento();

        FirmaDigitalService.Sesion sesion = firma.abrirSesion("cert-udes-2026.pfx");
        byte[] documentoFirmado = firma.firmar(sesion, documentoFinal);
        firma.cerrarSesion(sesion);

        correo.adjuntarArchivo(solicitud.getCorreoDestino(), documentoFirmado, "certificado-" + solicitud.getParticipanteId() + ".pdf");
        correo.enviar("Su certificado de participación", "Adjunto encontrará su certificado.");

        return documentoFirmado;
    }

    public boolean emitirYEnviar(String eventoId, String participanteId, String nombre, String correoDestino) {
        try {
            emitir(new SolicitudCertificado(eventoId, participanteId, nombre, correoDestino));
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
