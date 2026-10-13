package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmisionCertificadoTest {

    @Test
    void ordenColaboradorOrquestaLasCuatroEtapasSinExcepcion() {
        var validador = new ValidadorAsistencia();
        var generador = new GeneradorCertificadoPDF();
        var firma = new FirmaDigitalService();
        var correo = new EnvioCorreoService();

        // Instanciamos tu solución Facade de la Necesidad 2
        ServicioCertificadosFacade colaborador = new ServicioCertificadosFacade(validador, generador, firma, correo);

        // La operación simple expuesta debe ejecutarse sin lanzar excepciones
        assertDoesNotThrow(() -> {
            colaborador.emitirYEnviar("EVT-001", "PART-123", "Ana Ríos", "ana@correo.com");
        });
    }

    @Test
    void controladorCertificadosSoloDependeDeUnColaborador() {
        // Verificación de diseño: ControladorCertificados debe declarar un solo
        // constructor con un solo parámetro tras la refactorización.
        var constructores = ControladorCertificados.class.getDeclaredConstructors();
        assertEquals(1, constructores.length);
        assertEquals(1, constructores[0].getParameterCount());
    }
}
