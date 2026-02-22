<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/jsp/head.jsp"/>
</head>

<body id="page-top">
<div id="wrapper">
    <jsp:include page="/WEB-INF/jsp/nav_bar.jsp"/>

    <div id="content-wrapper" class="d-flex flex-column">
        <div id="content">
            <div class="container-fluid mt-4">

                <!-- Mensajes -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <div class="row">
                    <!-- COLUMNA IZQUIERDA: Productos -->
                    <div class="col-lg-9 col-md-8 col-sm-12">
                        <div class="card shadow mb-4">
                            <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
                                <span>📦 Detalles del Presupuesto</span>
                                <small class="badge bg-light text-dark">Paso 2: Agregar productos</small>
                            </div>
                            <div class="card-body">

                                <!-- BUSCADOR -->
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <label class="form-label fw-semibold">🔍 Buscar producto</label>
                                        <input type="text"
                                               id="buscarProducto"
                                               class="form-control form-control-lg"
                                               placeholder="Escribí el nombre o código del producto..."
                                               autocomplete="off">
                                        <div id="resultados"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1000; max-height: 400px; overflow-y: auto;"></div>
                                    </div>
                                </div>

                                <!-- DATOS DEL PRODUCTO -->
                                <div class="row mb-3">
                                    <div class="col-md-3">
                                        <label class="form-label">Stock disponible</label>
                                        <input type="text" id="stock" class="form-control" readonly>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Cantidad *</label>
                                        <input type="number" id="cantidad" class="form-control" min="1" value="1">
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Precio unitario</label>
                                        <input type="text" id="precio" class="form-control" readonly>
                                        <small class="text-muted" id="textoPrecio"></small>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Descuento (%)</label>
                                        <input type="number"
                                               id="descuento"
                                               class="form-control"
                                               value="0"
                                               min="0"
                                               max="100"
                                               step="0.01">
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end">
                                        <button class="btn btn-success w-100" onclick="agregarProducto()">
                                            + Agregar
                                        </button>
                                    </div>
                                </div>

                                <!-- TABLA DE ITEMS -->
                                <div class="table-responsive">
                                    <table class="table table-bordered table-hover">
                                        <thead class="table-dark">
                                        <tr>
                                            <th>Producto</th>
                                            <th class="text-center">Cant.</th>
                                            <th class="text-end">Precio Unit.</th>
                                            <th class="text-center">Desc. %</th>
                                            <th class="text-end">Subtotal</th>
                                            <th class="text-center" style="width: 60px;"></th>
                                        </tr>
                                        </thead>
                                        <tbody id="detallePresupuesto">
                                        <tr>
                                            <td colspan="6" class="text-center text-muted py-4">
                                                No hay productos agregados. Buscá y agregá productos arriba.
                                            </td>
                                        </tr>
                                        </tbody>
                                        <tfoot class="table-secondary">
                                        <tr>
                                            <td colspan="4" class="fw-bold text-end">SUBTOTAL (Efectivo):</td>
                                            <td class="text-end fw-bold fs-5 text-dark">
                                                $<span id="subtotalEfectivo">0.00</span>
                                            </td>
                                            <td></td>
                                        </tr>
                                        </tfoot>
                                    </table>
                                </div>

                            </div>
                        </div>
                    </div>

                    <!-- COLUMNA DERECHA: Datos generales -->
                    <div class="col-lg-3 col-md-4 col-sm-12">
                        <div class="card shadow mb-4">
                            <div class="card-header bg-success text-white">
                                <h6 class="mb-0">📝 Datos del Presupuesto</h6>
                            </div>
                            <div class="card-body">

                                <form id="formPresupuesto"
                                      method="post"
                                      action="${pageContext.request.contextPath}/presupuestos/guardar"
                                      onsubmit="return validarPresupuesto()">

                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                                    <!-- PASO 1: CLIENTE -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            <small class="badge bg-secondary">Paso 1</small>
                                            Cliente (opcional)
                                        </label>
                                        <input type="text"
                                               id="buscarCliente"
                                               class="form-control"
                                               placeholder="Buscar cliente..."
                                               autocomplete="off">
                                        <input type="hidden" name="clienteId" id="clienteId">

                                        <div id="resultadosCliente"
                                             class="list-group position-absolute w-100"
                                             style="z-index:1050; max-height:200px; overflow-y:auto;"></div>
                                        <small class="text-muted">Dejá vacío para "Consumidor Final"</small>
                                    </div>

                                    <hr>

                                    <!-- RESUMEN DE PRODUCTOS -->
                                    <div class="mb-3">
                                        <small class="text-muted">Productos agregados:</small>
                                        <div class="fs-5 fw-bold text-primary">
                                            <span id="cantidadItems">0</span> items
                                        </div>
                                    </div>

                                    <!-- SUBTOTAL PRELIMINAR -->
                                    <div class="mb-3">
                                        <small class="text-muted">Subtotal (efectivo):</small>
                                        <div class="fs-4 fw-bold text-dark">
                                            $<span id="subtotalGeneral">0.00</span>
                                        </div>
                                    </div>

                                    <hr>

                                    <!-- PASO 3: FORMA DE PAGO -->
                                    <div class="mb-3">
                                        <label class="form-label fw-semibold">
                                            <small class="badge bg-warning text-dark">Paso 3</small>
                                            💳 Forma de pago *
                                        </label>
                                        <select name="formaPago"
                                                id="formaPago"
                                                class="form-select form-select-lg"
                                                required
                                                onchange="actualizarPreciosFinal()">
                                            <option value="">-- Seleccionar método de pago --</option>
                                            <option value="CONTADO">💵 Efectivo </option>
                                            <option value="TARJETA">💳 Tarjeta </option>
                                            <option value="CUENTA_CORRIENTE">📋 Cuenta Corriente </option>
                                        </select>
                                    </div>

                                    <!-- TOTAL FINAL -->
                                    <div class="mb-4 p-3 bg-light rounded">
                                        <small class="text-muted">TOTAL A PRESUPUESTAR:</small>
                                        <div class="fs-2 fw-bold text-success">
                                            $<span id="totalFinal">0.00</span>
                                        </div>
                                        <small class="text-muted" id="detalleRecargo"></small>
                                    </div>

                                    <!-- Inputs hidden para enviar los items -->
                                    <div id="itemsHidden"></div>

                                    <button type="submit"
                                            class="btn btn-success btn-lg w-100 mb-2"
                                            id="btnGuardar"
                                            disabled>
                                        💾 Guardar Presupuesto
                                    </button>
                                    <small class="text-muted d-block text-center mt-2" id="mensajeAyuda">
                                        ⬆️ Agregá productos primero
                                    </small>

                                    <a href="${pageContext.request.contextPath}/presupuestos"
                                       class="btn btn-outline-secondary w-100 mt-2">
                                        Cancelar
                                    </a>

                                </form>

                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

<script>
let items = [];
let productoSeleccionado = null;
let clienteSeleccionado = null;
let productoDescripcion = "";
let precioContado = 0;
let precioTarjeta = 0;
let precioCC = 0;

// ==========================================
// AGREGAR PRODUCTO A LA LISTA
// ==========================================
function agregarProducto() {
    if (!productoSeleccionado) {
        alert("⚠️ Seleccioná un producto primero");
        return;
    }

    let cantidad = parseInt(document.getElementById("cantidad").value);
    let stock = parseInt(document.getElementById("stock").value);
    let descuentoPct = parseFloat(document.getElementById("descuento").value || 0);

    // Validaciones
    if (!cantidad || cantidad <= 0) {
        alert("⚠️ La cantidad debe ser mayor a 0");
        return;
    }

    if (cantidad > stock) {
        if (!confirm(`⚠️ La cantidad solicitada (${cantidad}) supera el stock disponible (${stock}). ¿Continuar igual?`)) {
            return;
        }
    }

    // Buscar si el producto ya existe
    let itemExistente = items.find(i => i.productoId === productoSeleccionado);

    if (itemExistente) {
        // Actualizar cantidad del producto existente
        itemExistente.cantidad += cantidad;
    } else {
        // Agregar nuevo producto
        items.push({
            productoId: productoSeleccionado,
            descripcion: productoDescripcion,
            cantidad: cantidad,
            precioContado: precioContado,
            precioTarjeta: precioTarjeta,
            precioCC: precioCC,
            descuento: descuentoPct
        });
    }

    // Limpiar campos
    limpiarSeleccion();
    renderTabla();
    actualizarPreciosFinal();
}

function limpiarSeleccion() {
    productoSeleccionado = null;
    productoDescripcion = "";
    document.getElementById("buscarProducto").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("cantidad").value = "1";
    document.getElementById("descuento").value = "0";
    document.getElementById("textoPrecio").textContent = "";
    document.getElementById("buscarProducto").focus();
}

// ==========================================
// RENDERIZAR TABLA
// ==========================================
function renderTabla() {
    let tbody = document.getElementById("detallePresupuesto");
    let hidden = document.getElementById("itemsHidden");

    tbody.innerHTML = "";
    hidden.innerHTML = "";

    if (items.length === 0) {
        tbody.innerHTML =
            "<tr>" +
                "<td colspan='6' class='text-center text-muted py-4'>" +
                    "No hay productos agregados.<br>Buscá y agregá productos arriba." +
                "</td>" +
            "</tr>";

        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "⬆️ Agregá productos primero";

    } else {
        items.forEach((item, index) => {
            // Usar precio en efectivo para mostrar en tabla
            let precio = item.precioContado;
            let subtotal = item.cantidad * precio * (1 - item.descuento / 100);

            tbody.innerHTML +=
                "<tr>" +
                    "<td><strong>" + item.descripcion + "</strong></td>" +
                    "<td class='text-center'>" + item.cantidad + "</td>" +
                    "<td class='text-end'>$" + precio.toFixed(2) + "</td>" +
                    "<td class='text-center'>" +
                        (item.descuento > 0 ? item.descuento + "%" : "-") +
                    "</td>" +
                    "<td class='text-end fw-semibold'>$" + subtotal.toFixed(2) + "</td>" +
                    "<td class='text-center'>" +
                        "<button type='button' class='btn btn-danger btn-sm' onclick='eliminarItem(" + index + ")'>✕</button>" +
                    "</td>" +
                "</tr>";

            // Inputs hidden (guardar IDs para el backend)
            hidden.innerHTML +=
                "<input type='hidden' name='productoIds' value='" + item.productoId + "'>" +
                "<input type='hidden' name='cantidades' value='" + item.cantidad + "'>" +
                "<input type='hidden' name='descuentos' value='" + item.descuento + "'>";
        });

        verificarHabilitarBoton();
    }

    document.getElementById("cantidadItems").textContent = items.length;
}

// ==========================================
// ACTUALIZAR PRECIOS SEGÚN FORMA DE PAGO
// ==========================================
function actualizarPreciosFinal() {
    const formaPago = document.getElementById("formaPago").value;
    let totalEfectivo = 0;
    let totalFinal = 0;

    items.forEach(item => {
        let precio = item.precioContado; // Base

        // Seleccionar precio según forma de pago
        if (formaPago === "TARJETA") {
            precio = item.precioTarjeta;
        } else if (formaPago === "CUENTA_CORRIENTE") {
            precio = item.precioCC;
        }

        let subtotal = item.cantidad * precio * (1 - item.descuento / 100);
        totalFinal += subtotal;

        // Calcular también el total en efectivo
        let subtotalEfectivo = item.cantidad * item.precioContado * (1 - item.descuento / 100);
        totalEfectivo += subtotalEfectivo;
    });

    document.getElementById("subtotalEfectivo").textContent = totalEfectivo.toFixed(2);
    document.getElementById("subtotalGeneral").textContent = totalEfectivo.toFixed(2);
    document.getElementById("totalFinal").textContent = totalFinal.toFixed(2);

    // Mostrar detalle de recargo
    let detalleRecargo = "";
    if (formaPago === "TARJETA") {
        let recargo = totalFinal - totalEfectivo;
        detalleRecargo = "Precio con tarjeta";
    } else if (formaPago === "CUENTA_CORRIENTE") {
        let recargo = totalFinal - totalEfectivo;
        detalleRecargo = "Precio con cuenta corriente";
    } else if (formaPago === "CONTADO") {
        detalleRecargo = "Precio en efectivo";
    }

    document.getElementById("detalleRecargo").textContent = detalleRecargo;

    verificarHabilitarBoton();
}

// ==========================================
// VERIFICAR SI SE PUEDE GUARDAR
// ==========================================
function verificarHabilitarBoton() {
    const hayProductos = items.length > 0;
    const hayFormaPago = document.getElementById("formaPago").value !== "";

    if (hayProductos && hayFormaPago) {
        document.getElementById("btnGuardar").disabled = false;
        document.getElementById("mensajeAyuda").textContent = "✅ Todo listo para guardar";
        document.getElementById("mensajeAyuda").className = "text-success d-block text-center mt-2 fw-bold";
    } else if (hayProductos && !hayFormaPago) {
        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "💳 Seleccioná la forma de pago";
        document.getElementById("mensajeAyuda").className = "text-warning d-block text-center mt-2";
    } else {
        document.getElementById("btnGuardar").disabled = true;
        document.getElementById("mensajeAyuda").textContent = "⬆️ Agregá productos primero";
        document.getElementById("mensajeAyuda").className = "text-muted d-block text-center mt-2";
    }
}

function eliminarItem(index) {
    if (confirm("¿Eliminar este producto?")) {
        items.splice(index, 1);
        renderTabla();
        actualizarPreciosFinal();
    }
}

function validarPresupuesto() {
    if (items.length === 0) {
        alert("⚠️ Agregá al menos un producto");
        return false;
    }

    if (!document.getElementById("formaPago").value) {
        alert("⚠️ Seleccioná la forma de pago");
        return false;
    }

    // Deshabilitar botón para evitar doble click
    document.getElementById("btnGuardar").disabled = true;
    document.getElementById("btnGuardar").textContent = "Guardando...";

    return true;
}

// ==========================================
// BÚSQUEDA DE PRODUCTOS
// ==========================================
document.getElementById("buscarProducto").addEventListener("keyup", function() {
    let q = this.value;

    if (q.length < 2) {
        document.getElementById("resultados").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/productos/buscar?q=" + encodeURIComponent(q))
        .then(res => res.json())
        .then(data => {
            let html = "";

            if (data.length === 0) {
                html = '<div class="list-group-item text-muted">No se encontraron productos</div>';
            } else {
                data.forEach(p => {
                    let stock = p.cantidad || 0;
                    let badgeClass = stock <= 5 ? 'bg-danger' : stock <= 20 ? 'bg-warning text-dark' : 'bg-success';

                    html +=
                        "<a href='#' class='list-group-item list-group-item-action producto-item' " +
                        "data-id='" + p.id + "' " +
                        "data-descripcion='" + (p.descripcion || '') + "' " +
                        "data-stock='" + stock + "' " +
                        "data-precio-contado='" + (p.precioContado || 0) + "' " +
                        "data-precio-tarjeta='" + (p.precioTarjeta || 0) + "' " +
                        "data-precio-cc='" + (p.precioCuentaCorriente || 0) + "'>" +
                        "<strong>" + (p.descripcion || 'Sin nombre') + "</strong>" +
                        "<br><small class='text-muted'>" +
                            "Efectivo: $" + (p.precioContado || 0) + " | " +
                            "Stock: <span class='badge " + badgeClass + "'>" + stock + "</span>" +
                        "</small>" +
                        "</a>";
                });
            }

            document.getElementById("resultados").innerHTML = html;
        })
        .catch(err => {
            console.error('Error buscando productos:', err);
        });
});

document.getElementById("resultados").addEventListener("click", function(e) {
    e.preventDefault();
    let item = e.target.closest(".producto-item");
    if (!item) return;

    seleccionarProducto(
        item.dataset.id,
        item.dataset.descripcion,
        item.dataset.stock,
        item.dataset.precioContado,
        item.dataset.precioTarjeta,
        item.dataset.precioCc
    );
});

function seleccionarProducto(id, descripcion, stock, pContado, pTarjeta, pCC) {
    productoSeleccionado = Number(id);
    productoDescripcion = descripcion;
    precioContado = parseFloat(pContado);
    precioTarjeta = parseFloat(pTarjeta);
    precioCC = parseFloat(pCC);

    document.getElementById("buscarProducto").value = descripcion;
    document.getElementById("stock").value = Number(stock);
    document.getElementById("cantidad").value = "1";
    document.getElementById("precio").value = pContado;
    document.getElementById("textoPrecio").textContent =
        `Tarjeta: $${pTarjeta} | C/C: $${pCC}`;

    document.getElementById("resultados").innerHTML = "";
    document.getElementById("cantidad").focus();
}

// ==========================================
// BÚSQUEDA DE CLIENTES
// ==========================================
document.getElementById("buscarCliente").addEventListener("keyup", function() {
    let q = this.value;

    if (q.length < 2) {
        document.getElementById("resultadosCliente").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/clientes/buscar?q=" + encodeURIComponent(q))
        .then(res => res.json())
        .then(data => {
            let html = "";

            data.forEach(c => {
                html +=
                    "<a href='#' class='list-group-item list-group-item-action cliente-item' " +
                    "data-id='" + c.id + "' " +
                    "data-nombre='" + (c.nombre || '') + "' " +
                    "data-apellido='" + (c.apellido || '') + "'>" +
                    (c.nombre || '') + " " + (c.apellido || '') +
                    "</a>";
            });

            document.getElementById("resultadosCliente").innerHTML = html;
        });
});

document.getElementById("resultadosCliente").addEventListener("click", function(e) {
    e.preventDefault();
    let item = e.target.closest(".cliente-item");
    if (!item) return;

    seleccionarCliente(
        item.dataset.id,
        item.dataset.nombre,
        item.dataset.apellido
    );
});

function seleccionarCliente(id, nombre, apellido) {
    clienteSeleccionado = id;
    document.getElementById("clienteId").value = id;
    document.getElementById("buscarCliente").value = nombre + " " + apellido;
    document.getElementById("resultadosCliente").innerHTML = "";
}

// ==========================================
// ESCUCHAR CAMBIO DE FORMA DE PAGO
// ==========================================
document.getElementById("formaPago").addEventListener("change", actualizarPreciosFinal);

// Cerrar resultados al hacer clic fuera
document.addEventListener('click', function(e) {
    if (!e.target.closest('#buscarProducto') && !e.target.closest('#resultados')) {
        document.getElementById('resultados').innerHTML = '';
    }
    if (!e.target.closest('#buscarCliente') && !e.target.closest('#resultadosCliente')) {
        document.getElementById('resultadosCliente').innerHTML = '';
    }
});
</script>

<style>
/* Responsive */
@media (max-width: 1000px) {
    .col-lg-9, .col-lg-3 {
        flex: 0 0 100%;
        max-width: 100%;
        margin-top: 1rem;
    }

    table.table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
        -webkit-overflow-scrolling: touch;
    }
}

/* Resultados búsqueda */
#resultados, #resultadosCliente {
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
    border-radius: 4px;
}

.producto-item, .cliente-item {
    cursor: pointer;
    transition: background-color 0.2s;
}

.producto-item:hover, .cliente-item:hover {
    background-color: #f0f9ff !important;
}
</style>

</body>
</html>