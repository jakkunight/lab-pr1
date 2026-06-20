#set page(paper: "a4", margin: 2cm)
#set text(font: "Liberation Serif", size: 11pt)

#align(center)[
    #text(
        size: 18pt,
        weight: "bold",
    )[Proyecto: Sistema de Gestión de Préstamos “FinanzaSegura”]
    #linebreak()
    Diagrama Entidad–Relación (Modelo Lógico)
]

#v(1em)

= Entidades

== Cliente
- *id*: PK, entero
- *cedula*: texto, único
- *telefono*: texto
- *email*: texto
- *nombre*: texto
- *direccion*: texto

== Sucursal
- *id*: PK, entero
- *codigo*: texto, único
- *nombre*: texto
- *direccion*: texto

== Prestamo
- *id*: PK, entero
- *monto*: entero
- *interes*: decimal
- *fecha_otorgamiento*: fecha
- *sucursal_id*: FK → Sucursal.id

== Cuota
- *prestamo_id*: PK, FK → Prestamo.id
- *numero*: PK, entero
- *es_fija*: booleano
- *monto*: entero
- *fecha_vencimiento*: fecha
- *estado*: enum(Activo, Cancelado, Anulado)

== Pago
- *id*: PK, entero
- *fecha_pago*: fecha
- *monto*: entero
- *medio*: enum(Efectivo, Transferencia, Tarjeta)
- *cliente_id*: FK → Cliente.id
- *prestamo_id*: FK → Prestamo.id

= Relaciones

- Un *Cliente* puede realizar *muchos Pagos*.
- Un *Prestamo* pertenece a *una Sucursal*.
- Un *Prestamo* tiene *muchas Cuotas*.
- Un *Prestamo* puede tener *muchos Pagos*.
- Un *Pago* es realizado por *un Cliente*.

#v(1em)

= Modelo ER (texto)

```text
Cliente (1) -------- (N) Pago (N) -------- (1) Prestamo
                                     |
                                     |
                                    (N)
                                   Cuota

Sucursal (1) -------- (N) Prestamo
```
