package com.universidad.confudes.certificados;

public class TraduccionInglesDecorator extends CertificadoDecorator {

    public TraduccionInglesDecorator(ServicioCertificados decorado) {
        super(decorado);
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] doc = super.emitir(solicitud);
        return UtilidadesPDF.traducirAIngles(doc);
    }
}
