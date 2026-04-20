# 🛒 Sistema de Gestión Comercial - Tienda de Electricidad

Aplicación web completa para la gestión de una tienda de electricidad, desarrollada con Spring Boot. Permite administrar stock, ventas y flujo comercial en tiempo real.



## 🚀 Demo
👉 https://youtu.be/IBmQhOAkXSs



## ⚙️ Tecnologías
- Java + Spring Boot
- Spring Security (autenticación y roles)
- Spring Data JPA / Hibernate
- MySQL
- JSP + Bootstrap
- Docker
- VPS (deploy productivo)



## 🧩 Funcionalidades

- Gestión de productos, clientes y proveedores  
- Control de stock en tiempo real  
- Presupuestos → conversión a remitos → ventas  
- Reportes de ganancias y ventas  
- Generación de comprobantes  
- Autenticación con roles (admin / empleado)  



## 🧠 Lo más importante (diferencial)

- Implementación de flujo comercial completo (tipo sistema real)  
- Arquitectura en capas (Controller - Service - Repository)  
- Optimización de consultas (evitando N+1 con JOIN FETCH)  
- Manejo de transacciones con @Transactional  
- Deploy en VPS con Docker  



## 🚀 Ejecución con Docker

1. Clonar el repositorio
```bash
git clone https://github.com/lucasbarreradev/electricidad
cd proyecto
```
2. Levantar el sistema
```bash
ADMIN_PASS=1234 EMPLEADO_PASS=12345 docker-compose up --build -d
```
3. Acceder a la aplicación
```bash
http://localhost:8080/electricidad
```

## ✒️ Autor
Lucas Barrera
* [LinkedIn](https://www.linkedin.com/in/lucas-barrera-dev)
