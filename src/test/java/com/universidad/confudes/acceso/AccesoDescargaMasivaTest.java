package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

class AccesoDescargaMasivaTest {

    @AfterEach
    void limpiarRol() {
        System.clearProperty("confudes.rol");
    }

    private ServicioCertificados crearServicioReal() {
        return solicitud -> new byte[0];
    }

    @Test
    void rechazaAParticipanteSinLlegarAEmitir() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        ServicioCertificados controlado = new ControlAccesoCertificadosProxy(crearServicioReal());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
    }

    @Test
    void permiteAOrganizador() {
        System.setProperty("confudes.rol", "ORGANIZADOR");
        ServicioCertificados controlado = new ControlAccesoCertificadosProxy(crearServicioReal());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }
}
