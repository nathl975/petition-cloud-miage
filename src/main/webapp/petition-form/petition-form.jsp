<!DOCTYPE html>
<html>
    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ page import="org.example.HelloAppEngine" %>
    <head>
    <title>Formulaire d'édition de pétition avec Bootstrap</title>
    <link rel="stylesheet" href="petition-form.css">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">

    <script defer src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/jwt-decode@3.1.2/build/jwt-decode.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/mithril@2.2.2/mithril.min.js"></script>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    </head>
    <body>
    <script>
        var tags = ["Justice", "Environnement", "Politique"]; // Tableau des tags

        var PetitionForm = {
    view: function() {
        return m(".container", [
        m("h2", "Formulaire d'édition de pétition"),
        m("form", [
            m(".form-group", [
            m("label", { for: "titre" }, "Titre de la pétition:"),
            m("input.form-control", { type: "text", id: "titre", name: "titre", required: true })
            ]),
            m(".form-group", [
            m("label", { for: "tags" }, "Tags associés:"),
            m("div", { style: "display: flex;" }, tags.map(function(tag) {
                return m(".badgeTag", tag);
            }))
            ]),
            m(".form-group", [
            m("label", { for: "contenu" }, "Contenu de la pétition:"),
            m("textarea.form-control", { id: "contenu", name: "contenu", rows: 8, required: true })
            ]),
            m("button.btn.btn-primary", { type: "submit" }, "Soumettre")
        ])
        ]);
    }
    };

m.mount(document.body, PetitionForm);
</script>  
</body>
</html>
