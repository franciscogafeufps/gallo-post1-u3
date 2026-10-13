package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

@Service
public class ServicioAsistenciaAdapter implements ServicioAsistencia {

    private final QRCheckClient client;

    public ServicioAsistenciaAdapter() {
        this.client = new QRCheckClient();
    }

    public ServicioAsistenciaAdapter(QRCheckClient client) {
        this.client = client;
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        long idEventoParsed;
        try {
            idEventoParsed = Long.parseLong(eventoId.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            idEventoParsed = 1L;
        }

        QRCheckRequest request = new QRCheckRequest(credencialQR, idEventoParsed);
        QRCheckResponse response = client.validar(request);

        boolean exitoso = response.getCodigoRespuesta() == 200;
        return new ResultadoCheckIn(exitoso, response.getDetalle());
    }
}
