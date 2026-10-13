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
    │   ├── certificados/                 # Necesidades 2 y 3 (Facade y Decorator)
    │   │   ├── ServicioCertificados.java
    │   │   ├── SolicitudCertificado.java
    │   │   ├── UtilidadesPDF.java
    │   │   ├── ValidadorAsistencia.java
    │   │   ├── GeneradorCertificadoPDF.java
    │   │   ├── FirmaDigitalService.java
    │   │   ├── EnvioCorreoService.java
    │   │   ├── ControladorCertificados.java
    │   │   ├── ServicioCertificadosFacade.java
    │   │   ├── CertificadoDecorator.java
    │   │   ├── MarcaAguaDecorator.java
    │   │   ├── CodigoQRDecorator.java
    │   │   └── TraduccionInglesDecorator.java
    │   └── acceso/                       # Necesidad 4 (Patrón Proxy)
    │       ├── ContextoUsuario.java
    │       └── ControlAccesoCertificadosProxy.java
    └── test/java/com/universidad/confudes/
        ├── asistencia/
        │   └── CheckInIntegracionTest.java
        ├── certificados/
        │   ├── EmisionCertificadoTest.java
        │   └── MejorasCertificadoTest.java
        └── acceso/
            └── AccesoDescargaMasivaTest.java
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
  - **Implementación:** Clase `ServicioCertificadosFacade` que orquesta la secuencia de negocio (`ValidadorAsistencia` → `GeneradorCertificadoPDF` → `FirmaDigitalService` → `EnvioCorreoService`) y expone una operación simplificada (`emitir`) para `ControladorCertificados`.
* **Alternativa considerada y descartada:** **Adapter (Adaptador)**
* **Justificación técnica:**
  1. **Naturaleza del problema:** `ControladorCertificados` sufría de un acoplamiento excesivo: inyectaba cuatro dependencias separadas y gestionaba directamente el ciclo de vida de la sesión de firma digital, la generación de bytes del PDF y el adjunto de correos. Cualquier cambio en la infraestructura de firma o envío requería modificar el controlador web. No obstante, las APIs de los cuatro servicios no eran incompatibles; funcionaban adecuadamente y continúan siendo utilizadas de forma independiente por otros módulos de ConfUDES.
  2. **Por qué Facade resuelve el problema:** El patrón Facade proporciona una interfaz unificada y sencilla para un conjunto complejo de subsistemas. `ServicioCertificadosFacade` encapsula todo el flujo transaccional en el método `emitir(SolicitudCertificado)`, reduciendo las dependencias del controlador de cuatro a una sola, y disminuyendo la lógica de `ControladorCertificados.emitir()` a menos de 10 líneas de código limpio.
  3. **Por qué se descarta Adapter:** Adapter se enfoca en resolver incompatibilidad de interfaces entre contratos preexistentes (típicamente 1 a 1). En este caso, no existía un contrato previo que los servicios necesitaran cumplir ni problemas de incompatibilidad de tipos; el síntoma residía en el exceso de colaboradores y la fuga de detalles de orquestación hacia la capa web. Aplicar Adapter no tendría sentido porque no se requería traducir una firma a otra, sino encapsular un flujo multicomponente bajo una fachada de más alto nivel.

---

### Necesidad 3 — Mejoras opcionales y combinables del certificado
* **Patrón aplicado:** **Decorator (Decorador)**
  - **Implementación:** Clase abstracta `CertificadoDecorator` y decoradores concretos `MarcaAguaDecorator`, `CodigoQRDecorator` y `TraduccionInglesDecorator`, apilables sobre cualquier implementación de `ServicioCertificados`.
* **Alternativas consideradas y descartadas:** **Herencia estática**, **Parámetros booleanos (banderas)** y **Proxy**.
* **Justificación técnica:**
  1. **Naturaleza del problema:** Se requería aplicar tres mejoras opcionales e independientes (marca de agua, QR de validación y traducción al inglés) sobre el documento PDF generado. Cada evento puede requerir ninguna, una o cualquier combinación de las tres en cualquier orden (8 combinaciones posibles con 3 mejoras, escalando exponencialmente a $2^N$).
  2. **Por qué se descarta la Herencia:** Resolver las combinaciones mediante subclases produciría una explosión combinatoria inmanejable ($2^N$ clases). Para 3 mejoras se requerirían 8 subclases distintas (`CertificadoConMarcaDeAgua`, `CertificadoConQR`, `CertificadoConMarcaYQR`, etc.), y para 5 mejoras ascendería a 32 clases. Además, la herencia es estática en tiempo de compilación y no permite activar/desactivar características de forma dinámica en tiempo de ejecución.
  3. **Por qué se descartan los Parámetros Booleanos:** Agregar banderas booleanas (`boolean conMarca`, `boolean conQR`, `boolean enIngles`) en la firma de `emitir()` violaría el Principio de Responsabilidad Única (SRP) y el Principio Abierto/Cerrado (OCP). Cada nueva mejora exigiría modificar la firma del método en toda la base de código y añadir condicionales anidados dentro del método base.
  4. **Por qué no serviría el patrón Proxy (de la Necesidad 4):** Aunque estructuralmente ambos envuelven `ServicioCertificados`, la intención de Proxy es controlar el acceso o sustituir al objeto real, no componer capacidades adicionales sobre el resultado de forma recursiva e intercambiable.
  5. **Por qué Decorator es la solución ideal:** Con Decorator sólo se requiere **una clase por cada mejora** ($N$ clases en lugar de $2^N$). Los decoradores envuelven dinámicamente al servicio base en memoria (`new MarcaAguaDecorator(new CodigoQRDecorator(base))`), permitiendo apilar tantas mejoras como se desee sin alterar el colaborador base `ServicioCertificadosFacade`.

---

### Necesidad 4 — Control de acceso a una operación costosa y restringida
* **Patrón aplicado:** **Proxy (Proxy de Protección / Protection Proxy)**
  - **Implementación:** Clase `ControlAccesoCertificadosProxy` que implementa `ServicioCertificados` y verifica el rol del usuario actual a través de `ContextoUsuario.rolActual()` antes de delegar en el servicio real.
* **Alternativa considerada y descartada:** **Decorator (Decorador)**
* **Justificación técnica:**
  1. **Naturaleza del problema:** La descarga masiva de certificados implica invocar el servicio de firma digital, limitado por el proveedor a 60 llamadas por minuto. La operación es costosa y debe restringirse exclusivamente a usuarios con rol `ORGANIZADOR` o `ADMIN`. Si un participante intenta la operación, debe ser rechazado inmediatamente con una excepción de seguridad sin llegar a ejecutar ninguna llamada al servicio de firma.
  2. **Por qué Proxy resuelve el problema:** El patrón Proxy proporciona un sustituto o intermediario que controla el acceso al objeto real. `ControlAccesoCertificadosProxy` intercepta la solicitud, inspecciona el rol en `ContextoUsuario` y, si el usuario no tiene permisos suficientes, lanza un `SecurityException` de forma inmediata. La invocación al `servicioReal.emitir(solicitud)` nunca llega a ocurrir para roles no autorizados, protegiendo la cuota del proveedor externo.
  3. **Por qué se descarta Decorator (Comparación sutil con Necesidad 3):**
     - **Intención:** Decorator tiene como propósito *añadir responsabilidades adicionales* al resultado y siempre termina delegando en el objeto subyacente para enriquecer su salida. Proxy, en cambio, tiene como propósito *controlar y condicionar el acceso*, decidiendo si delega o aborta la ejecución.
     - **Incompatibilidad técnica:** Si se utilizara un Decorator para control de acceso, por su naturaleza de procesamiento de salida ejecutaría primero la emisión real (la operación costosa de firma) antes de evaluar los permisos, consumiendo la cuota restringida del proveedor aun cuando el usuario no estuviera autorizado.

---

### Reflexión — Composite y Flyweight (Opcional)
* **Composite:** Encajaría de manera natural en la administración de la agenda de cada congreso de ConfUDES. La agenda tiene una estructura jerárquica de árbol (Congreso → Tracks → Sesiones → Actividades/Charlas), donde tanto los contenedores (Tracks, Sesiones) como las hojas individuales (Actividades) comparten operaciones comunes (como calcular duración total o listar horarios). Composite permitiría tratarlos a todos de forma uniforme bajo una misma interfaz `ComponenteAgenda`.
* **Flyweight:** No aplica para las credenciales QR generadas. El patrón Flyweight está diseñado para ahorrar memoria compartiendo estado intrínseco idéntico entre miles de objetos. Sin embargo, cada credencial QR en ConfUDES almacena datos estrictamente únicos e irrepetibles por participante (identificador, firma y hash criptográfico único), por lo que carece de estado intrínseco compartible que justifique la sobrecarga del patrón.

---

## Herramientas utilizadas
- **Java 17** (Amazon Corretto / OpenJDK LTS)
- **Spring Boot 3.2.0**
- **Apache Maven 3.8+**
- **JUnit 5** (Jupiter)
- **Visual Studio Code / IntelliJ IDEA**
- **Git & GitHub**

---

## Conclusiones
La implementación de este laboratorio evidenció que la selección de un patrón estructural no depende de su diagrama de clases (donde patrones como Decorator y Proxy son estructuralmente equivalentes), sino de la **intención de diseño** que cada uno persigue. Mientras que Adapter resolvió la incompatibilidad 1 a 1 entre contratos preexistentes y Facade redujo la complejidad de orquestar múltiples colaboradores, la combinación de Decorator y Proxy demostró cómo extender funcionalidades de manera aditiva e interceptar el flujo de ejecución para protección de recursos costosos sin vulnerar el principio Open/Closed ni acoplar a los clientes a detalles de implementación.
