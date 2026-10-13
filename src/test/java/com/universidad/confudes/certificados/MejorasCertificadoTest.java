package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {

    private final SolicitudCertificado solicitud =
            new SolicitudCertificado("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");

    private ServicioCertificados crearBase() {
        return new ServicioCertificadosFacade(
                new ValidadorAsistencia(),
                new GeneradorCertificadoPDF(),
                new FirmaDigitalService(),
                new EnvioCorreoService()
        );
    }

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = crearBase();
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados base = crearBase();
        ServicioCertificados conTodo = new MarcaAguaDecorator(
                new CodigoQRDecorator(
                        new TraduccionInglesDecorator(base)
                )
        );
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados base = crearBase();
        ServicioCertificados soloMarcaDeAgua = new MarcaAguaDecorator(base);
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }
}
