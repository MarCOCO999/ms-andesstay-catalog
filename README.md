# ms-andesstay-catalog

Microservicio de catálogo del proyecto **AndesStay** (caso DSY1107 – Desarrollo Cloud Native I, Duoc UC): administra las unidades (habitaciones y cabañas), su tarifa, disponibilidad, valores agregados y foto de referencia.

## Rol en la arquitectura

- Fuente de verdad de la disponibilidad e inventario de unidades. `ms-andesstay-reservations` consulta la tarifa vigente y reserva/libera unidades llamando a este servicio, nunca modificando disponibilidad por su cuenta.
- La disminución/aumento de disponibilidad se hace con **operaciones atómicas a nivel de base de datos** (`UPDATE ... WHERE availableUnits > 0`), para evitar sobreventa (overbooking) ante llamadas concurrentes.
- Los "valores agregados" (wifi, desayuno, vista al lago, etc.) son un **enum cerrado** (`Amenity`), expuesto vía `/api/catalog/amenities`, para eliminar errores de tipeo o comparación entre frontend y backend.
- La foto de referencia de cada unidad se sube **directo del navegador a Amazon S3** mediante una URL prefirmada que este servicio genera (`/api/catalog/units/images/presign`): el binario de la imagen nunca pasa por el backend.

## Stack

- Spring Boot 4.1.1 / Java 21
- Spring Data JPA + Oracle Database (driver `ojdbc17`)
- Spring Security OAuth2 Resource Server (JWT de Azure AD)
- AWS SDK for Java v2 (`S3Presigner`, incluido en el artifact `s3`) — usa las credenciales del rol de la instancia EC2 (o de un perfil AWS local para desarrollo), nunca una clave embebida en el código

## Configuración

| Variable | Descripción | Default |
|---|---|---|
| `SERVER_PORT` | Puerto HTTP | `8083` |
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | Conexión a Oracle | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` / `andesstay` / `changeit` |
| `JPA_DDL_AUTO` | Estrategia de Hibernate | `update` |
| `S3_IMAGES_BUCKET` | Bucket S3 para las fotos de las unidades | `andesstay-images-<account-id>` |
| `AWS_REGION` | Región del bucket | `us-east-1` |
| `S3_PRESIGN_TTL_SECONDS` | Vigencia de la URL prefirmada de subida | `300` |
| `AZURE_ISSUER_URI` / `AZURE_API_AUDIENCE` | Validación del JWT de Azure AD | tenant/App ID URI del caso |

## Endpoints principales

| Método | Ruta | Roles | Descripción |
|---|---|---|---|
| `GET` | `/api/catalog/units` | Cliente, Operador, Admin | Lista unidades (filtros por tipo/activa) |
| `GET` | `/api/catalog/units/{id}` | Cliente, Operador, Admin | Detalle de una unidad |
| `POST` | `/api/catalog/units` | Admin | Crea una unidad |
| `PUT` | `/api/catalog/units/{id}` | Admin | Actualiza tarifa, disponibilidad, activa, amenities, foto |
| `POST` | `/api/catalog/units/{id}/reserve` | Operador, Admin | Decrementa disponibilidad atómicamente (llamado por `reservations`) |
| `POST` | `/api/catalog/units/{id}/release` | Operador, Admin | Incrementa disponibilidad (llamado por `reservations`) |
| `POST` | `/api/catalog/units/images/presign` | Admin | Genera una URL prefirmada de S3 para subir la foto de una unidad |
| `GET` | `/api/catalog/amenities` | Cliente, Operador, Admin | Lista cerrada de valores agregados válidos |

## Cómo ejecutarlo en local

Requiere Java 21 y Oracle levantado (ver [andesstay-infra](https://github.com/MarCOCO999/andesstay-infra)). La subida de fotos requiere además credenciales de AWS válidas en el entorno (por ejemplo `AWS_PROFILE`); sin ellas, el resto del servicio funciona normal.

```bash
./mvnw spring-boot:run
```

## Repos relacionados

[frontend-andesstay](https://github.com/MarCOCO999/frontend-andesstay) · [ms-andesstay-bff](https://github.com/MarCOCO999/ms-andesstay-bff) · [ms-andesstay-reservations](https://github.com/MarCOCO999/ms-andesstay-reservations) · [ms-andesstay-notify](https://github.com/MarCOCO999/ms-andesstay-notify) · [ms-andesstay-audit](https://github.com/MarCOCO999/ms-andesstay-audit) · [ms-andesstay-report](https://github.com/MarCOCO999/ms-andesstay-report) · [andesstay-infra](https://github.com/MarCOCO999/andesstay-infra)
