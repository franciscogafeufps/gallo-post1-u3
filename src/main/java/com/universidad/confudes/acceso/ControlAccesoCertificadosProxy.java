package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;

public class ControlAccesoCertificadosProxy implements ServicioCertificados {

    private final ServicioCertificados servicioReal;

    public ControlAccesoCertificadosProxy(ServicioCertificados servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (!"ORGANIZADOR".equalsIgnoreCase(rol) && !"ADMIN".equalsIgnoreCase(rol)) {
            throw new SecurityException("Acceso denegado: solo usuarios con rol ORGANIZADOR o ADMIN pueden emitir certificados en esta operacion.");
        }
        return servicioReal.emitir(solicitud);
    }
}
