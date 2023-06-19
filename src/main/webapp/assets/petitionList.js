var Petitions = {
    list: [],
    loadList: function() {
        return m.request({
            method: "GET",
            url: "/petitions"
        })
        .then(function(result) {
            Petitions.list = result
            console.log("got:",result)
        })
    }
}
var testPetitions = [
    {
        id: 1,
        owner: "John Doe",
        body: "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
        description: "Petition Test",
        tags: [1, 2],
        date: "2023-06-19",
        nbSignatures: 10
    },
    {
        id: 2,
        owner: "Jane Smith",
        body: "Praesent et dignissim lorem. Vestibulum finibus lectus eu lacinia euismod.",
        description: "Petition 2",
        tags: [2,3],
        date: "2023-06-20",
        nbSignatures: 5
    },
    {
        id: 3,
        owner: "Michael Johnson",
        body: "Nulla ultrices nibh ut diam consectetur, nec efficitur sem consequat.",
        description: "Petition 3",
        tags: [1,3],
        date: "2023-06-21",
        nbSignatures: 15
    }
];

var PetitionList = {
    oncreate: function() {
        Petitions.loadList(); // Appel de la méthode loadList lors de la création du composant
    },
    view: function() {
        return m(".container", [
            m("h2", "Liste des pétitions"),
            testPetitions.map(function(petition) {
                return m(".petition.card", { style: "margin-top: 10px" },  [ // Ajoutez la classe "card" pour encadrer la pétition
                    m("h3.card-title", { style: "font-weight: bold; padding: 10px;" }, petition.description), //
                    m("p.card-body", petition.body), // Contenu de la pétition
                    m(".container", { style: "display : flex ; justify-content : space-between ; margin-bottom : 5px" }, [
                        m("div", "Publié par "+ petition.owner + " le " + petition.date), // Auteur de la pétition
                        m("div", "Nombre de signatures: " + petition.nbSignatures), // Nombre de signatures
                    ]),
                ]);
            })
        ]);
    }
}