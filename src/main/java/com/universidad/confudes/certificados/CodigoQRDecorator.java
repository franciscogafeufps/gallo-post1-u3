package com.universidad.confudes.certificados;

public class CodigoQRDecorator extends CertificadoDecorator {
    private final String urlVerificacion;

    public CodigoQRDecorator(ServicioCertificados decorado) {
        this(decorado, "https://confudes.edu.co/verificar");
    }

    public CodigoQRDecorator(ServicioCertificados decorado, String urlVerificacion) {
        super(decorado);
        this.urlVerificacion = urlVerificacion;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] doc = super.emitir(solicitud);
        String url = this.urlVerificacion.contains("verificar") && !this.urlVerificacion.endsWith("/")
                ? this.urlVerificacion + "/" + solicitud.getParticipanteId()
                : this.urlVerificacion;
        return UtilidadesPDF.insertarCodigoQR(doc, url);
    }
}
