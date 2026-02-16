<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.List" %>
<%@ page import="com.sistema.model.Producto" %>

<%
List<Producto> productos = (List<Producto>) request.getAttribute("productos");
String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <jsp:include page="/WEB-INF/jsp/head.jsp"/>
</head>

<body id="page-top">
<div id="wrapper">
<jsp:include page="/WEB-INF/jsp/nav_bar.jsp"/>

<div class="container-fluid mt-4">
<div class="row">

    <!-- IZQUIERDA -->
    <div class="col-lg-9 col-md-8 col-sm-12">
        <div class="card shadow mb-4">
            <div class="card-header bg-primary text-white">
                🛒 Detalles de la venta
            </div>
            <div class="card-body">

                <!-- BUSCADOR -->
                <div class="row mb-3">
                    <div class="col-md-12">
                        <input type="text"
                               id="buscarProducto"
                               class="form-control"
                               placeholder="Escribí el nombre del producto...">
                               <div id="resultados"
                                    class="list-group position-absolute w-100"
                                    style="z-index:1000"></div>
                    </div>
                </div>

                <!-- DATOS PRODUCTO -->
                <div class="row mb-3">
                    <div class="col-md-3">
                        <label>Stock</label>
                        <input type="text" id="stock" class="form-control" readonly>
                    </div>
                    <div class="col-md-3">
                        <label>Cantidad</label>
                        <input type="number" id="cantidad" class="form-control" min="1" max="0">
                    </div>
                    <div class="col-md-3">
                        <label>Precio</label>
                        <input type="text" id="precio" class="form-control" readonly>
                    </div>
                    <div class="col-md-3">
                        <label>Descuento (%)</label>
                        <input type="number"
                               id="descuento"
                               class="form-control"
                               value="0"
                               min="0"
                               max="100">
                    </div>
                </div>

                <button class="btn btn-success mb-3"
                        onclick="agregarProducto()">
                    + Agregar
                </button>

                <!-- TABLA -->
                <table class="table table-bordered">
                    <thead>
                    <tr>
                        <th>Producto</th>
                        <th>Cant.</th>
                        <th>Precio</th>
                        <th>Desc.</th>
                        <th>Subtotal</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody id="detalleVenta"></tbody>
                </table>

            </div>
        </div>
    </div>

    <!-- DERECHA -->
    <div class="col-lg-3 col-md-4 col-sm-12 mt-3 mt-md-0">
        <div class="card shadow mb-4">
            <div class="card-header bg-success text-white">
                📝 Datos generales
            </div>

            <div class="card-body">

                <form method="post" action="${pageContext.request.contextPath}/ventas/guardar">
                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}"/>

                   <label>Cliente</label>

                   <select name="clienteId" class="form-control mb-3">

                       <!-- Consumidor Final -->
                       <option value=""
                           ${venta.cliente == null ? "selected" : ""}>
                           Consumidor Final
                       </option>

                       <!-- Lista de clientes -->
                       <c:forEach items="${clientes}" var="c">
                           <option value="${c.id}"
                               ${venta.cliente != null && venta.cliente.id == c.id ? "selected" : ""}>
                               ${c.nombre} ${c.apellido}
                           </option>
                       </c:forEach>

                   </select>


                    <!-- FORMA DE PAGO -->
                    <label>Forma de pago</label>
                    <select name="formaPago" class="form-control mb-3">
                        <option value="CONTADO">Contado</option>
                        <option value="TARJETA">Tarjeta</option>
                        <option value="CUENTA_CORRIENTE">Cuenta Corriente</option>
                    </select>

                    <!-- RESUMEN -->
                    <div class="mb-3">
                        <small class="text-muted">Productos:</small>
                        <div class="fs-5 fw-bold text-primary">
                            <span id="cantidadItems">0</span> items
                        </div>
                    </div>

                    <div class="mb-4">
                        <small class="text-muted">TOTAL A PAGAR:</small>
                        <div class="fs-3 fw-bold text-success">
                            $<span id="total">0.00</span>
                        </div>
                    </div>

                    <div id="itemsHidden"></div>

                    <button type="submit" class="btn btn-success w-100">
                        💾 Guardar Venta
                    </button>

                </form>

            </div>
        </div>
    </div>

</div>
<script>
let items = [];
let productoSeleccionado = null;
let productoDescripcion = "";

function agregarProducto() {
    if (!productoSeleccionado) {
        alert("Seleccione un producto");
        return;
    }

    let cantidad = parseInt(document.getElementById("cantidad").value);
    let precio = parseFloat(document.getElementById("precio").value);
    let descuentoPct = parseFloat(document.getElementById("descuento").value || 0);
    let stock = parseInt(document.getElementById("stock").value);

    // Validaciones
    if (!cantidad || cantidad <= 0) {
        alert("La cantidad debe ser mayor a 0");
        return;
    }

    // 🔹 Buscar si el producto ya existe en items
    let itemExistente = items.find(i => i.productoId === productoSeleccionado);

    let cantidadTotal = cantidad;
    if (itemExistente) {
        cantidadTotal += itemExistente.cantidad;
    }

    if (cantidadTotal > stock) {
        alert(
            `Stock Insuficiente.`
        );
        return;
    }

    let subtotalBruto = cantidad * precio;
    let descuentoMonto = subtotalBruto * (descuentoPct / 100);
    let subtotal = subtotalBruto - descuentoMonto;

    if (itemExistente) {
        // actualizar cantidad
        itemExistente.cantidad += cantidad;

        // recalcular subtotal sobre la cantidad total
        itemExistente.subtotal = itemExistente.cantidad * itemExistente.precio * (1 - itemExistente.descuento / 100);
    } else {
        // agregar nuevo producto
        items.push({
            productoId: productoSeleccionado,
            descripcion: productoDescripcion,
            cantidad: cantidad,
            precio: precio,
            descuento: descuentoPct,
            subtotal: subtotal
        });
    }


    // Reset inputs
    productoSeleccionado = null;
    productoDescripcion = "";
    document.getElementById("buscarProducto").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("cantidad").max = stock;

    renderTabla();
}


function renderTabla() {
    let tbody = document.getElementById("detalleVenta");
    let hidden = document.getElementById("itemsHidden");
    let total = 0;

    tbody.innerHTML = "";
    hidden.innerHTML = "";

    if (items.length === 0) {
        tbody.innerHTML =
            "<tr>" +
                "<td colspan='6' class='text-center text-muted py-4'>" +
                    "No hay productos agregados. Buscá y agregá productos arriba." +
                "</td>" +
            "</tr>";
    } else {
        items.forEach((i, index) => {
            total += i.subtotal;

            tbody.innerHTML +=
                "<tr>" +
                    "<td>" + i.descripcion + "</td>" +
                    "<td>" + i.cantidad + "</td>" +
                    "<td>" + i.precio.toFixed(2) + "</td>" +
                    "<td>" + i.descuento + "%</td>" +
                    "<td>" + i.subtotal.toFixed(2) + "</td>" +
                    "<td><button type='button' class='btn btn-danger btn-sm' onclick='eliminar(" + index + ")'>X</button></td>" +
                "</tr>";

            hidden.innerHTML +=
                "<input type='hidden' name='productoIds' value='" + i.productoId + "'>" +
                "<input type='hidden' name='cantidades' value='" + i.cantidad + "'>" +
                "<input type='hidden' name='descuentos' value='" + i.descuento + "'>" +
                "<input type='hidden' name='precios' value='" + i.precio + "'>";
        });
    }

    document.getElementById("total").textContent = total.toFixed(2);
    document.getElementById("cantidadItems").textContent = items.length;
}



function eliminar(i) {
    items.splice(i, 1);
    renderTabla();
}

let precioContado = 0;
let precioTarjeta = 0;
let precioCC = 0;

document.getElementById("buscarProducto").addEventListener("keyup", function () {
    let q = this.value;

    if (q.length < 2) {
        document.getElementById("resultados").innerHTML = "";
        return;
    }

    fetch("${pageContext.request.contextPath}/productos/buscar?q=" + encodeURIComponent(q))
        .then(res => res.json())
        .then(data => {
            let html = "";

            data.forEach(p => {
                let stock = p.cantidad ?? 0;
                let precioContado = p.precioContado ?? 0;
                let precioTarjeta = p.precioTarjeta ?? 0;
                let precioCC = p.precioCuentaCorriente ?? 0;

                html +=
                    "<a href='#' class='list-group-item list-group-item-action producto-item' " +
                    "data-id='" + p.id + "' " +
                    "data-descripcion='" + p.descripcion + "' " +
                    "data-stock='" + stock + "' " +
                    "data-precio-contado='" + p.precioContado + "' " +
                    "data-precio-tarjeta='" + p.precioTarjeta + "' " +
                    "data-precio-cc='" + p.precioCuentaCorriente + "'>" +
                    p.descripcion + " | Stock: " + stock +
                    "</a>";
            });

            document.getElementById("resultados").innerHTML = html;
        });
});

document.getElementById("resultados").addEventListener("click", function (e) {
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

    productoSeleccionado = id;
    productoDescripcion = descripcion;

    precioContado = parseFloat(pContado);
    precioTarjeta = parseFloat(pTarjeta);
    precioCC = parseFloat(pCC);

    document.getElementById("buscarProducto").value = descripcion;
    document.getElementById("stock").value = stock;
    document.getElementById("cantidad").value = 1;

    actualizarPrecioSegunFormaPago();

    document.getElementById("resultados").innerHTML = "";

    document.getElementById("cantidad").focus();
}

function actualizarPrecioSegunFormaPago() {

    let formaPago = document.querySelector("select[name='formaPago']").value;
    let precio = 0;

    if (formaPago === "CONTADO") {
        precio = precioContado;
    } else if (formaPago === "TARJETA") {
        precio = precioTarjeta;
    } else if (formaPago === "CUENTA_CORRIENTE") {
        precio = precioCC;
    }

    document.getElementById("precio").value = precio.toFixed(2);
}
document.querySelector("select[name='formaPago']")
    .addEventListener("change", function () {
        actualizarPrecioSegunFormaPago();
    });


renderTabla();

</script>

</body>

<style>
@media (max-width: 1000px) {
    /* Columnas apilan */
    .row.m-4 > .col-lg-9,
    .row.m-4 > .col-lg-3 {
        flex: 0 0 100%;
        max-width: 100%;
        margin-top: 1rem;
    }

    /* Tabla responsive horizontal */
    table.table {
        display: block;
        overflow-x: auto;
        white-space: nowrap;
        -webkit-overflow-scrolling: touch; /* scroll suave en móvil */
    }
}
</style>



</html>


