<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Gestión</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-card {
            max-width: 400px;
            width: 100%;
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="login-card p-5">
        <div class="text-center mb-4">
            <h2 class="fw-bold">🏪 Sistema de Gestión</h2>
            <p class="text-muted">Ingresá para continuar</p>
        </div>

        <!-- Mensajes -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        <c:if test="${not empty mensaje}">
            <div class="alert alert-success">${mensaje}</div>
        </c:if>

        <!-- Formulario -->
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="mb-3">
                <label class="form-label">Usuario</label>
                <input type="text" name="username" class="form-control"
                       placeholder="Ingresá tu usuario" required autofocus>
            </div>

            <div class="mb-4">
                <label class="form-label">Contraseña</label>
                <input type="password" name="password" class="form-control"
                       placeholder="Ingresá tu contraseña" required>
            </div>

            <button type="submit" class="btn btn-primary w-100 mb-3">
                Iniciar Sesión
            </button>

            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>

        <div class="text-center text-muted small">
            <p class="mb-0">Sistema de Gestión v1.0</p>
        </div>
    </div>
</body>
</html>
