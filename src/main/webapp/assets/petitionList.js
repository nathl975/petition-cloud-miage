var Petitions = {
    list: [],
    loadList: function() {
        return m.request({
            method: "GET",
            url: "/petitions/"
        })
        .then(function(result) {
            Petitions.list = result
            console.log("got:",result)
        })
    }
}
var Tags = {
    list: [],
    loadList: function() {
        return m.request({
            method: "GET",
            url: "/tags/"
        })
        .then(function(result) {
            Tags.list = result
            console.log("got:",result)
        })
    },
    getTagName: function(tagId) {
        var tag = Tags.list.find(tag => tag.id === tagId);
        return tag ? tag.name : null;
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
    tagList: [],
    filterOption: "",
    
    oncreate: function() {
        Petitions.loadList();
        Tags.loadList();
    },
    toggleTag: function(tagId) {
        var index = PetitionList.tagList.indexOf(tagId);
        if (index === -1) {
            PetitionList.tagList.push(tagId); // Ajoute le tag sélectionné
        } else {
            PetitionList.tagList.splice(index, 1); // Supprime le tag désélectionné
        }
    },
    isTagSelected: function(tagId) {
        return PetitionList.tagList.indexOf(tagId) !== -1;
    },
    
    handleFilterChange: function(event) {
        PetitionList.filterOption = event.target.value;
    },
    
    view: function() {
        return m(".container", [
            m("div", {style:"display:flex;height : 20px"},[
                m("select", { onchange: PetitionList.handleFilterChange },  [
                    m("option", { value: "" }, "Tous"),
                    m("option", { value: "mostSigned" }, "Les plus signées"),
                    m("option", { value: "mostRecent" }, "Les plus récentes"),
                    m("option", { value: "mySigned" }, "Mes pétitions signées")
                ]),
                m(".form-group", [
                    m("label", { for: "tags" }, "Tags :"),
                    m("div", { style: "display: flex;" }, Tags.list.map(function(tag) {
                        return m(".badgeTag", {
                            class: PetitionList.isTagSelected(tag.id) ? "selected" : "", // Ajoute une classe CSS si le tag est sélectionné
                            onclick: function() {
                                PetitionList.toggleTag(tag.id); // Appelle la fonction toggleTag lors du clic sur un tag
                            }
                        }, tag.tagName);
                    }))
                ]),
            ]),

            Petitions.list.filter(function(petition) { // Remplacer testPetitions par Petitions.list
                if (PetitionList.filterOption === "mostSigned") {
                    return petition.nbSignatures > 0;
                } else if (PetitionList.filterOption === "mostRecent") {
                    return new Date(petition.date) > new Date();
                } else if (PetitionList.filterOption === "mySigned") {
                    return false;
                }
                return true;
            }).map(function(petition) {
                return m(".petition.card", { style: "margin-top: 10px" }, [
                    m("h3.card-title", { style: "font-weight: bold; padding: 10px;" }, petition.description),
                    m("p.card-body", petition.body),
                    m(".container", { style: "display: flex; justify-content: space-between; margin-bottom: 5px" }, [
                        m("div", "Tags: " + petition.tags.map(tagId => Tags.getTagName(tagId)).join(', ')),
                        m("div", "Publié par " + petition.owner + " le " + petition.date),
                        m("div", "Nombre de signatures: " + 0),
                    ]),
                ]);
            })
        ]);
    }
}