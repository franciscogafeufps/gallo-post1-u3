package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    private final ServicioCertificadosFacade servicioCertificados;

    public ControladorCertificados(ServicioCertificadosFacade servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId,
                                         @PathVariable String participanteId,
                                         @RequestParam String nombre,
                                         @RequestParam String correoDestino) {
        boolean exito = servicioCertificados.emitirYEnviar(eventoId, participanteId, nombre, correoDestino);
        if (!exito) {
            return ResponseEntity.status(403).body("Asistencia insuficiente");
        }
        return ResponseEntity.ok("Certificado emitido y enviado");
    }
}
