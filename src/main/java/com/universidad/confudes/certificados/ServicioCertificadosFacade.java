package com.universidad.confudes.certificados;

import org.springframework.stereotype.Service;

@Service
public class ServicioCertificadosFacade {

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

    public boolean emitirYEnviar(String eventoId, String participanteId, String nombre, String correoDestino) {
        if (!validador.tieneAsistenciaMinima(participanteId, eventoId, 0.8)) {
            return false;
        }

        byte[] doc = generador.iniciarDocumento("plantilla-2026");
        generador.insertarDatosParticipante(doc, nombre, eventoId, "2026-08-06");
        byte[] documentoFinal = generador.finalizarDocumento();

        FirmaDigitalService.Sesion sesion = firma.abrirSesion("cert-udes-2026.pfx");
        byte[] documentoFirmado = firma.firmar(sesion, documentoFinal);
        firma.cerrarSesion(sesion);

        correo.adjuntarArchivo(correoDestino, documentoFirmado, "certificado-" + participanteId + ".pdf");
        correo.enviar("Su certificado de participación", "Adjunto encontrará su certificado.");

        return true;
    }
}
