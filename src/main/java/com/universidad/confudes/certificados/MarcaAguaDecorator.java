package com.universidad.confudes.certificados;

public class MarcaAguaDecorator extends CertificadoDecorator {
    private final String texto;

    public MarcaAguaDecorator(ServicioCertificados decorado) {
        this(decorado, "ConfUDES Institucional");
    }

    public MarcaAguaDecorator(ServicioCertificados decorado, String texto) {
        super(decorado);
        this.texto = texto;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] doc = super.emitir(solicitud);
        return UtilidadesPDF.aplicarMarcaDeAgua(doc, texto);
    }
}
