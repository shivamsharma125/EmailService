# 📧 Email Service

This microservice handles **transactional email delivery** in an event-driven architecture. It listens to a **Kafka topic** for email events and sends emails using the **JavaMail API**.

---

## 🚀 Features

* ✅ Kafka-based event-driven communication
* ✅ Dynamic email sending on user signup or other triggers
* ✅ Utilizes JavaMail for SMTP email sending
* ✅ Secure authentication using environment variables
* ✅ Decoupled, testable architecture

---

## 🛠️ Tech Stack

* Java 17
* Spring Boot 3.x
* Apache Kafka
* JavaMail API
* Maven

---

## 📦 Kafka Topic

* **Topic Name:** `signup`
* **Group ID:** `EmailService`

Only one consumer from the `EmailService` group will handle a single event (thanks to Kafka's consumer group protocol).

---

## 🧩 Architecture Overview

```
+-------------+       Kafka Topic: signup       +--------------------------+
| Other       |  ----------------------------> | KafkaConsumerEmailClient |
| Microservice|                                +-----------+--------------+
+-------------+                                            |
                                                        [SendEmailDto]
                                                            |
                                                            v
                                                  +-------------------+
                                                  |    EmailUtil      |
                                                  +-------------------+
                                                  | JavaMail SMTP Send|
                                                  +-------------------+
```

---

## 📂 Main Components

### 1. **KafkaConsumerEmailClient**

```java
@KafkaListener(topics = "signup", groupId = "EmailService")
public void handleSendEmailEvent(String message) {
    // Deserializes JSON to SendEmailDto
    // Creates SMTP session
    // Sends email via EmailUtil
}
```

* Deserializes message using Jackson
* Configures SMTP
* Uses environment properties for credentials

### 2. **SendEmailDto**

```java
@Getter
@Setter
public class SendEmailDto {
    private String to;
    private String subject;
    private String body;
}
```

A DTO for transporting email details.

### 3. **EmailUtil**

```java
public static void sendEmail(Session session, String to, String subject, String body) {
    // Creates MIME message
    // Sends via JavaMail Transport
}
```

Handles actual email sending using SMTP and JavaMail's `Transport.send()`.

---

## 🔐 Environment Configuration

Set the following in `application.properties` or as environment variables:

```properties
sender.email=your_email@gmail.com
sender.password=your_app_password
```

> ⚠️ **Use App Passwords** (not your real password) if using Gmail.

---

## ✅ Sample Email Event (JSON)

This is the JSON format expected in the Kafka message:

```json
{
  "to": "user@example.com",
  "subject": "Welcome to E-Commerce Platform!",
  "body": "Thank you for signing up. We're excited to have you!"
}
```

---

## 🧪 Testing

To test the service:

1. **Produce a Kafka message** to the `signup` topic using  a mock producer.

2. **Observe logs**

    * Should see: `Message is ready`, then `Email Sent Successfully!!`

3. **Check your email inbox** for delivery confirmation.

---

## 📌 Future Enhancements

* Add retry mechanism for email failures
* Store email logs in database
* HTML templating support
* Add support for other triggers (password reset, order confirmation)

---

## 📌 Related Microservices

* 🛒 **Product Service** – Manage product catalog
* 👤 **User Service** – Handle user registration and authentication
* 💳 **Payment Service** – Manage payments via Razorpay & Stripe
* 🔍 **Service Discovery** – Eureka-based service registry

---