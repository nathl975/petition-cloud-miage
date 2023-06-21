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
var User = {
    signatures: [],
    loadSignatures: function() {
        const jwtToken = localStorage.getItem('jwt');
        if (jwtToken) {
            return m.request({
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + jwtToken
                },
                url: "/users/signatures",
            })
                .then(function(result) {
                    User.signatures = result;
                });
        } else {
            User.signatures = [];
        }
    },
    hasSigned: function(petitionId) {
        return User.signatures.some(signature => signature.petition === petitionId);
    },
}

var PetitionList = {
    list: [],
    tagList: [],
    filterOption: "",
    filteredList: [],

    loadList: function(url, headers = {}) {
        return m.request({
            method: "GET",
            headers: headers,
            url: url
        })
            .then(function (result) {
                PetitionList.list = result
                console.log("got:", result)
            })
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
        const jwtToken = localStorage.getItem('jwt');
        return m(".container", [
            m("div", {style:"display:flex;height : 25px"},[
                m("select", { onchange: PetitionList.handleFilterChange },  [
                    m("option", { value: "" }, "Tous"),
                    m("option", { value: "mostSigned" }, "Les plus signées"),
                    m("option", { value: "mostRecent" }, "Les plus récentes"),
                    m("option", { value: "mySigned" }, "Mes pétitions signées")
                ]),
            ]),
                m("div", {style: "display: flex;margin-top : 5px"}, Tags.list.map(function (tag) {
                    return m(".badge .badge-secondary", {
                        class: PetitionList.isTagSelected(tag.id) ? "badge badge-info" : "", // Ajoute une classe CSS si le tag est sélectionné
                        style: "cursor:pointer;margin-right:2px",
                        onclick: function () {
                            PetitionList.toggleTag(tag.id); // Appelle la fonction toggleTag lors du clic sur un tag
                        }
                    }, tag.tagName);
                })),


            PetitionList.list.sort(function(a, b) {
                if (PetitionList.filterOption === "mostSigned") {
                    return b.signatureCount - a.signatureCount;
                } else if (PetitionList.filterOption === "mostRecent") {
                    const dateA = new Date(a.date);
                    const dateB = new Date(b.date);
                    return dateB - dateA;
                }
                return true;
            })
                .filter(function(petition) {
                    if (PetitionList.tagList.length === 0) {
                        return true;
                    }

                    var numTagList = PetitionList.tagList.map(Number);

                    if (PetitionList.filterOption === "mySigned") {
                        return User.hasSigned(petition.id) && numTagList.some(tagId => petition.tags.includes(tagId));
                    } else {
                        return numTagList.some(tagId => petition.tags.includes(tagId));
                    }
                })

                .map(function(petition) {
                return m(".petition.card", { style: "margin-top: 10px" }, [
                    m("h3", { style: "font-weight: bold; margin: 5px;" }, petition.description),
                    m("div" ,{style:"margin:5px;max-height:100px;word-break:breakword"},petition.body),
                    m("div", { style: "display: flex; justify-content: space-between; margin: 0px 0px 2px 5px;font-size:10px",class:"card-subtitle text-muted" }, [
                        m("div", "Tags: " + petition.tags.map(tagId => Tags.getTagName(tagId)).join(', ')),
                        m("div", "Publié par " + petition.owner + " le " + petition.date),
                        m("div", "Nombre de signatures: " + petition.signatureCount),
                        m("div", { style: "display: flex" }, [
                            m("form", { action: "/visualisation-petition/"+petition.id }, [
                                m("button", {
                                    type: "submit",
                                    style:"margin-right:2px",
                                    class: "btn btn-primary"
                                }, "Consulter")
                            ]),
                            m("button", {
                                disabled: !jwtToken || User.hasSigned(petition.id),
                                class:"btn btn-primary",
                                onclick: function() {
                                    m.request({
                                        method: "POST",
                                        headers: {
                                            'Content-Type': 'application/json',
                                            'Authorization': 'Bearer ' + jwtToken
                                        },
                                        url: "/petitions/" + petition.id + "/signatures",
                                    })
                                        .then(function(result) {
                                            // Gérer la réponse
                                            User.loadSignatures();  // Mettre à jour les signatures de l'utilisateur
                                        })
                                }
                            }, User.hasSigned(petition.id) ? "Signée" : "Signer")
                        ])
                    ]),
                ]);
            })
        ]);
    }
}
var MyPetitionsList = {
    oncreate: function() {
        const jwtToken = localStorage.getItem('jwt');
        if (jwtToken) {
            PetitionList.loadList("/petitions/user", {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + jwtToken
            });
            Tags.loadList();
            User.loadSignatures();
        }
    },
    view: function() {
        return PetitionList.view();
    }
}

var HomePageList = {
    oncreate: function() {
        PetitionList.loadList("/petitions/");
        Tags.loadList();
        const jwtToken = localStorage.getItem('jwt');
        if (jwtToken) {
            User.loadSignatures();
        }
    },
    view: function() {
        return PetitionList.view();
    }
}

