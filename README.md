# Post-contenido — Unidad 3: Patrones Estructurales en ConfUDES

## Descripción
Repositorio del post-contenido de la Unidad 3 de Patrones de Diseño de Software. Proyecto Spring Boot (`confudes-patrones-estructurales`) que resuelve los requerimientos del backend de **ConfUDES**, una plataforma académica para gestionar la asistencia y la certificación en congresos universitarios.

El sistema aborda cuatro necesidades reales aplicando patrones de diseño estructurales del catálogo GoF, garantizando el desacoplamiento, la extensibilidad y el respeto riguroso por el código legado y de terceros sin modificar las clases dadas.

---

## Cómo ejecutar

### Prerrequisitos
- **Java 17** o superior instalado y configurado en el `PATH`.
- **Apache Maven 3.8+** instalado.

### Comandos de ejecución y pruebas
```bash
# Compilar y empaquetar el proyecto
mvn clean package

# Ejecutar la aplicación Spring Boot
mvn spring-boot:run

# Ejecutar las pruebas unitarias y de integración (JUnit 5)
mvn test
```

---

## Estructura del Proyecto

```text
gallo-post1-u3/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/universidad/confudes/
    │   ├── ConfUdesApp.java
    │   ├── externo/qrcheck/              # SDK de terceros (Inalterable)
    │   │   ├── QRCheckClient.java
    │   │   ├── QRCheckRequest.java
    │   │   └── QRCheckResponse.java
    │   ├── asistencia/                   # Necesidad 1 (Patrón Adapter)
    │   │   ├── ServicioAsistencia.java
    │   │   ├── ResultadoCheckIn.java
    │   │   ├── ControladorCheckIn.java
    │   │   └── ServicioAsistenciaAdapter.java
    │   └── certificados/                 # Necesidad 2 (Patrón Facade)
    │       ├── ValidadorAsistencia.java
    │       ├── GeneradorCertificadoPDF.java
    │       ├── FirmaDigitalService.java
    │       ├── EnvioCorreoService.java
    │       ├── ControladorCertificados.java
    │       └── ServicioCertificadosFacade.java
    └── test/java/com/universidad/confudes/
        ├── asistencia/
        │   └── CheckInIntegracionTest.java
        └── certificados/
            └── EmisionCertificadoTest.java
```

---

## Decisiones de diseño

### Necesidad 1 — Registro de asistencia con un proveedor externo
* **Patrón aplicado:** **Adapter (Adaptador de Objetos)**
  - **Implementación:** Clase `ServicioAsistenciaAdapter` que implementa la interfaz del dominio `ServicioAsistencia` y compone una instancia de `QRCheckClient`.
* **Alternativa considerada y descartada:** **Facade (Fachada)**
* **Justificación técnica:**
  1. **Naturaleza del problema:** El controlador `ControladorCheckIn` y otros módulos del sistema ya dependían estrictamente del contrato existente `ServicioAsistencia` con la firma `registrarAsistencia(String eventoId, String participanteId, String credencialQR)`. Por su parte, la librería de terceros `QRCheckClient` ofrecía una interfaz incompatible: exigía una petición `QRCheckRequest(String payload, long idEvento)` y retornaba un `QRCheckResponse(int codigoRespuesta, String detalle)`.
  2. **Por qué Adapter resuelve el problema:** El patrón Adapter está concebido específicamente para convertir la interfaz de una clase en otra interfaz que el cliente espera, permitiendo que clases con interfaces incompatibles colaboren entre sí. `ServicioAsistenciaAdapter` actúa como puente: convierte el `eventoId` (String) a numérico (`long`), transfiere la credencial QR y transforma el código de respuesta HTTP (200 OK / 401 Unauthorized) en un objeto de dominio `ResultadoCheckIn`.
  3. **Por qué se descarta Facade:** Facade busca definir una interfaz *nueva, simple y de alto nivel* para ocultar la complejidad de un subsistema conformado por múltiples colaboradores. En este escenario no hay múltiples colaboradores ni complejidad de orquestación, sino un único proveedor externo cuya interfaz no coincide con el contrato previo obligatorio. Facade no encaja porque no tiene la responsabilidad de traducir tipos de datos ni de amoldarse a una interfaz preexistente e inalterable ya utilizada en producción.

---

### Necesidad 2 — Emisión de certificados con demasiados colaboradores
* **Patrón aplicado:** **Facade (Fachada)**
  - **Implementación:** Clase `ServicioCertificadosFacade` que orquesta la secuencia de negocio (`ValidadorAsistencia` → `GeneradorCertificadoPDF` → `FirmaDigitalService` → `EnvioCorreoService`) y expone una operación simplificada (`emitirYEnviar`) para `ControladorCertificados`.
* **Alternativa considerada y descartada:** **Adapter (Adaptador)**
* **Justificación técnica:**
  1. **Naturaleza del problema:** `ControladorCertificados` sufría de un acoplamiento excesivo: inyectaba cuatro dependencias separadas y gestionaba directamente el ciclo de vida de la sesión de firma digital, la generación de bytes del PDF y el adjunto de correos. Cualquier cambio en la infraestructura de firma o envío requería modificar el controlador web. No obstante, las APIs de los cuatro servicios no eran incompatibles; funcionaban adecuadamente y continúan siendo utilizadas de forma independiente por otros módulos de ConfUDES.
  2. **Por qué Facade resuelve el problema:** El patrón Facade proporciona una interfaz unificada y sencilla para un conjunto complejo de subsistemas. `ServicioCertificadosFacade` encapsula todo el flujo transaccional en un único método `emitirYEnviar`, reduciendo las dependencias del controlador de cuatro a una sola, y disminuyendo la lógica de `ControladorCertificados.emitir()` a menos de 10 líneas de código limpio.
  3. **Por qué se descarta Adapter:** Adapter se enfoca en resolver incompatibilidad de interfaces entre contratos preexistentes (típicamente 1 a 1). En este caso, no existía un contrato previo que los servicios necesitaran cumplir ni problemas de incompatibilidad de tipos; el síntoma residía en el exceso de colaboradores y la fuga de detalles de orquestación hacia la capa web. Aplicar Adapter no tendría sentido porque no se requería traducir una firma a otra, sino encapsular un flujo multicomponente bajo una fachada de más alto nivel.

---

### Necesidad 3 — Mejoras opcionales del certificado
*(Pendiente de implementación en Parte 2 - Patrón Decorator)*

---

### Necesidad 4 — Control de acceso a la descarga masiva
*(Pendiente de implementación en Parte 2 - Patrón Proxy)*

---

## Herramientas utilizadas
- **Java 17** (LTS)
- **Spring Boot 3.2.0**
- **Apache Maven 3.8+**
- **JUnit 5**
- **Git & GitHub**
