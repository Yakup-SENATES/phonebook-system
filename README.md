# Phonebook System (Telefon Rehberi Sistemi)

[TR] Bu proje, mikroservis mimarisi kullanılarak geliştirilmiş kapsamlı bir Telefon Rehberi ve Raporlama sistemidir.  
[EN] This project is a comprehensive Phonebook and Reporting system developed using microservices architecture.

---

## 🛠️ Teknolojiler ve Versiyonlar / Technologies & Versions

| [TR] Teknoloji | [EN] Technology | Versiyon / Version |
| :--- | :--- | :--- |
| **Java** | **Java** | 17 |
| **Spring Boot** | **Spring Boot** | 3.5.9 |
| **Veritabanı** | **Database** | PostgreSQL 17 |
| **Mesajlaşma** | **Messaging** | Apache Kafka (7.5.0) |
| **Nesne Eşleme** | **Object Mapping** | MapStruct 1.5.5 |
| **Migration** | **Migration** | Flyway |
| **İletişim** | **Communication** | Spring Cloud OpenFeign |

---

## 🏛️ Proje Mimarisi / Project Architecture

### 1. Contact Service (`:8080`)
[TR] Kişi ve iletişim bilgilerini yönetir. Veritabanı: `contactdb`.  
[EN] Manages person and contact information. Database: `contactdb`.

### 2. Report Service (`:8081`)
[TR] Rapor taleplerini işler ve sonuçları saklar. Veritabanı: `reportdb`.  
[EN] Processes report requests and stores results. Database: `reportdb`.

---

## 🔗 API Endpoints & JSON Examples

### Contact Service (`/api/persons`)

| Method | Endpoint | [TR] Açıklama | [EN] Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Yeni kişi oluşturur | Create new person |
| `PUT` | `/{id}` | Kişi bilgilerini günceller | Update person details |
| `DELETE` | `/{id}` | Kişiyi siler | Delete person |
| `GET` | `/` | Tüm kişileri listeler | List all persons |
| `GET` | `/{id}` | Kişi detaylarını getirir | Get person details |
| `POST` | `/{id}/contacts` | İletişim bilgisi ekler | Add contact info |
| `DELETE` | `/{id}/contacts/{cId}` | İletişim bilgisini siler | Remove contact info |
| `GET` | `/location-stats` | Konum istatistikleri | Location statistics |

#### 📝 Example Requests (JSON)

**Create Person (`POST /api/persons`):**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "company": "SETUR"
}
```

**Add Contact Info (`POST /api/persons/{id}/contacts`):**
```json
{
  "type": "PHONE",
  "value": "5551234567"
}
```
> [!NOTE]
> Types: `PHONE`, `EMAIL`, `LOCATION`

---

### Report Service (`/api/reports`)

| Method | Endpoint | [TR] Açıklama | [EN] Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/request` | Rapor talebi oluşturur | Request a new report |
| `GET` | `/list` | Raporları listeler | List all reports |
| `GET` | `/{id}` | Rapor detayını getirir | Get report detail |

---

## ⚡ Kafka Messaging Flow

[TR] Raporlama süreci asenkron çalışır: `contact-service` (Producer) -> `report-requests` topic -> `report-service` (Consumer).  
[EN] Reporting process is asynchronous: `contact-service` (Producer) -> `report-requests` topic -> `report-service` (Consumer).

---

## 📦 Database Schema / Veritabanı Şeması

### Contact DB (`t_person`, `t_contact_info`)
- `t_person`: `id`, `first_name`, `last_name`, `company`
- `t_contact_info`: `id`, `person_id`, `type`, `value`

### Report DB (`t_report`, `t_report_detail`)
- `t_report`: `id`, `request_date`, `status`
- `t_report_detail`: `id`, `report_id`, `location`, `person_count`, `phone_number_count`

---

## ⚙️ Setup & Run / Kurulum ve Çalıştırma

### [TR] Adımlar
1. Altyapıyı başlatın: `docker-compose up -d`
2. Servisleri çalıştırın: `mvn spring-boot:run`

### [EN] Steps
1. Start infrastructure: `docker-compose up -d`
2. Run services: `mvn spring-boot:run`
