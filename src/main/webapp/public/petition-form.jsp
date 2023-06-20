<!DOCTYPE html>
<html>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.HelloAppEngine" %>
<head>
    <title>Nouvelle pétition</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">

    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
            integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.12.9/dist/umd/popper.min.js"
            integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js"
            integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
            crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/jwt-decode@3.1.2/build/jwt-decode.min.js"></script>
    <script defer="" src="https://use.fontawesome.com/releases/v5.3.1/js/all.js"></script>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
    <script src="https://unpkg.com/mithril/mithril.js"></script>
    <script src="../../assets/navbar.js"></script>
</head>
<body>
<script>

    var Petition = {
        Title: "",
        Content: "",
        Tags: [],

        load: function () {

        },
        save: function () {
            var jwtToken = localStorage.getItem('jwt'); // Replace 'jwt-token' with the key you use to store the token
            fetch('/petitions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + jwtToken
                },
                body: JSON.stringify({
                    "body": Petition.Content,
                    "description": Petition.Title,
                    "tags": Petition.Tags.map(String),
                }),
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Not authenticated');
                    }
                })
                .catch((error) => {
                    console.error('Error:', error);
                });
        },
        toggleTag: function (tagId) {
            var index = Petition.Tags.indexOf(tagId);
            if (index === -1) {
                Petition.Tags.push(tagId); // Ajoute le tag sélectionné
            } else {
                Petition.Tags.splice(index, 1); // Supprime le tag désélectionné
            }
        },
        isTagSelected: function (tagId) {
            return Petition.Tags.indexOf(tagId) !== -1;
        }
    }
    var Tags = {
        list: [],
        loadList: function () {
            return m.request({
                method: "GET",
                url: "/tags/"
            })
                .then(function (result) {
                    Tags.list = result
                    console.log("got:", result)
                })
        }
    }
    var PetitionView = {
        oncreate: function () {
            // Vérification de l'ID dans l'URL
            var urlParams = new URLSearchParams(window.location.search);
            var id = urlParams.get('id');
            if (id) {
                // Si l'id d'une pétition est founi, on en charge le contenu
                Petition.load(id);
            }
            Tags.loadList(); // Appel de la méthode loadList lors de la création du composant
        },
        view: function () {
            return m("div.container", [
                m(Navbar),
                m('div.content', {style: 'margin: 10px;'}, [
                    m("h2", "Nouvelle pétition"),
                    m("form", {
                        onsubmit: function (event) {
                            event.preventDefault(); // Empêche le comportement par défaut du formulaire
                            Petition.save(); // Enregistre la pétition
                        }
                    }, [
                        m(".form-group", [
                            m("label", {for: "titre"}, "Titre de la pétition:"),
                            m("input.form-control", {
                                type: "text", id: "titre", name: "titre", required: true,
                                oninput: function (event) {
                                    Petition.Title = event.target.value; // Met à jour la valeur de Petition.Title
                                }
                            })
                        ]),
                        m(".form-group", [
                            m("label", {for: "tags"}, "Tags associés:"),
                            m("div", {style: "display: flex;"}, Tags.list.map(function (tag) {
                                return m(".badge .badge-secondary", {
                                    class: Petition.isTagSelected(tag.id) ? "badge badge-info" : "", // Ajoute une classe CSS si le tag est sélectionné
                                    onclick: function () {
                                        Petition.toggleTag(tag.id); // Appelle la fonction toggleTag lors du clic sur un tag
                                    }
                                }, tag.tagName);
                            }))
                        ]),
                        m(".form-group", [
                            m("label", {for: "contenu"}, "Contenu de la pétition:"),
                            m("textarea.form-control", {
                                id: "contenu", name: "contenu", rows: 8, required: true,
                                oninput: function (event) {
                                    Petition.Content = event.target.value; // Met à jour la valeur de Petition.Content
                                }
                            })
                        ]),
                        m("button.btn.btn-primary", {type: "submit"}, "Soumettre")
                    ])
                ]),
            ]);
        }
    };

    m.mount(document.body, PetitionView);
</script>
</body>
</html>
