package com.universidad.confudes.certificados;

public abstract class CertificadoDecorator implements ServicioCertificados {
    protected final ServicioCertificados decorado;

    public CertificadoDecorator(ServicioCertificados decorado) {
        this.decorado = decorado;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        return decorado.emitir(solicitud);
    }
}
