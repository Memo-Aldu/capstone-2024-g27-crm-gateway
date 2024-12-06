# CRM Gateway

The **CRM Gateway** is a key component in the **Universal CRM Suite**, acting as the entry point between the **frontend** and backend services. It securely handles user authentication via **Azure Active Directory (Azure AD)** and routes requests to the appropriate backend services for message management and contact management.

This gateway ensures secure communication between the frontend and backend by enforcing authentication and routing the API requests accordingly. It is built with **Spring Boot** and connects to the **SMS Management Service** and **Contact Management Service**.

## Table of Contents

- [Overview](#overview)
- [Configuration](#configuration)
- [Endpoints](#endpoints)
- [Azure AD Integration](#azure-ad-integration)
- [Running the Gateway Locally](#running-the-gateway-locally)
- [Deployment](#deployment)
- [Environment Variables](#environment-variables)
- [Contact](#contact)

## Overview

The **CRM Gateway** serves as the **API gateway** for managing incoming requests from the frontend and forwarding them to the relevant backend services. It handles secure routing based on the **Azure Active Directory** authentication and authorization, ensuring only authorized users can interact with the backend services.

### Key Features:
- **Azure AD Authentication**: Integrates with **Azure AD** to authenticate users and authorize access to the CRM services.
- **Service Routing**: Routes requests to **SMS Management Service** and **Contact Management Service** based on the requested API endpoint.
- **Security**: Ensures that only authorized users can access the services, enforcing security policies such as user group validation.

## Configuration

The CRM Gateway requires certain configuration settings to properly connect to the backend services and authenticate using **Azure AD**. These settings can be found in the `.env` file.

### Sample `.env.example`:

```env
# Server Configuration
SERVER_PORT=8080

# Azure AD Configuration
ALLOWED_GROUP_NAMES=group1
AZURE_AD_ENABLED=true
AZURE_TENANT_ID=some-tenant-id
AZURE_CLIENT_ID=some-client-id
AZURE_CLIENT_SECRET=some-client-secret
AZURE_APP_ID_URI=api://sms-management-api

# Service Routes
MESSAGE_SERVICE_ROUTE=http://host.docker.internal:8081
CONTACT_SERVICE_ROUTE=http://host.docker.internal:8082
```

### Key Configuration Variables:

- **SERVER_PORT**: Port on which the gateway service will run (default is 8080).
- **AZURE_AD_ENABLED**: Set to `true` to enable Azure Active Directory authentication.
- **ALLOWED_GROUP_NAMES**: Comma-separated list of Azure AD groups allowed to access the gateway.
- **AZURE_TENANT_ID**: The **Azure AD Tenant ID** used for authentication.
- **AZURE_CLIENT_ID**: The **Azure AD Client ID** for the gateway's application registration.
- **AZURE_CLIENT_SECRET**: The **client secret** for authenticating the application with Azure AD.
- **AZURE_APP_ID_URI**: The API URI for the SMS Management service, used in Azure AD's authorization flow.
- **MESSAGE_SERVICE_ROUTE**: The route to the **SMS Management Service**.
- **CONTACT_SERVICE_ROUTE**: The route to the **Contact Management Service**.

## Endpoints

The **CRM Gateway** exposes the following endpoints, which route requests to the respective services:

### **/api/v1/messages**  
Routes to the **SMS Management Service** for sending and managing SMS/MMS messages.

### **/api/v1/contacts**  
Routes to the **Contact Management Service** for managing contacts.

### **/api/v1/health**  
Health check endpoint to ensure the gateway is operational.

## Azure AD Integration

### Azure AD Authentication
The **CRM Gateway** uses **Azure Active Directory (Azure AD)** for authentication and authorization. Requests are validated against Azure AD groups, and only users in the allowed groups can access the gateway's endpoints.

1. **User Authentication**: When users try to access any of the backend services, they must authenticate via Azure AD. If they are not authenticated, they will receive a 401 Unauthorized response.
2. **Group-Based Authorization**: The gateway checks whether the authenticated user is part of one of the allowed Azure AD groups (`ALLOWED_GROUP_NAMES`). If not, access is denied with a 403 Forbidden response.

The integration relies on Azure’s OAuth2 system to authenticate and authorize users.


## Running the Gateway Locally

To run the **CRM Gateway** locally, follow these steps:

### Step 1: Clone the repository
```bash
git clone https://github.com/Memo-Aldu/capstone-2024-g27-crm-gateway.git
cd capstone-2024-g27-crm-gateway
```

### Step 2: Configure the environment
1. Create a `.env` file in the root of the project based on the `.env.example` file provided.
2. Update the Azure AD settings, such as `AZURE_TENANT_ID`, `AZURE_CLIENT_ID`, and `AZURE_CLIENT_SECRET`, with your specific Azure AD application details.
3. Update the service URLs (`MESSAGE_SERVICE_ROUTE`, `CONTACT_SERVICE_ROUTE`) to point to your local or containerized backend services.

### Step 3: Run the gateway locally
```bash
mvn spring-boot:run
```

The gateway will be available at `http://localhost:8080`. Test the API routes (e.g., `/api/v1/messages`, `/api/v1/contacts`) using tools like **Postman** or **cURL**.

## Deployment

The **CRM Gateway** can be deployed to cloud platforms like **Azure**, **AWS**, or **Google Cloud** using containers or virtual machines. Follow these steps for deployment:

### Step 1: Dockerize the Gateway
1. Create a **Dockerfile** in the root of the project directory.
2. Build the Docker image:
   ```bash
   docker build -t crm-gateway .
   ```

### Step 2: Deploy to Cloud
1. Push the Docker image to a container registry (e.g., **Docker Hub**, **Azure Container Registry**).
2. Deploy the image to your chosen platform (e.g., **Azure Container Apps**, **AWS ECS**).


## Environment Variables

Ensure the following environment variables are set correctly for both local and production environments:

- **SERVER_PORT**: Port on which the gateway service runs (default: `8080`).
- **AZURE_AD_ENABLED**: Enable or disable Azure AD authentication (`true` or `false`).
- **ALLOWED_GROUP_NAMES**: Comma-separated list of groups allowed to access the gateway.
- **AZURE_TENANT_ID**: The **Azure AD Tenant ID** for authentication.
- **AZURE_CLIENT_ID**: The **Azure AD Client ID** of the gateway's registered application.
- **AZURE_CLIENT_SECRET**: The **client secret** for Azure AD authentication.
- **AZURE_APP_ID_URI**: The API URI for SMS Management and Contact Management services.
- **MESSAGE_SERVICE_ROUTE**: URL to the **SMS Management Service**.
- **CONTACT_SERVICE_ROUTE**: URL to the **Contact Management Service**.

This **CRM Gateway** is crucial for handling secure communication and routing requests between the **frontend** and **backend services** in the **Universal CRM Suite**. The integration with **Azure AD** ensures that only authorized users can access the system's services, enhancing security and user management.


## Contact
For questions or support, please contact **Memo Aldujaili** at [maldu064@uottawa.ca](mailto:maldu064@uottawa.ca).
