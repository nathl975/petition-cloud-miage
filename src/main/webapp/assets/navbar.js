var credential = ""

let Login = {
    name:"",
    email:"",
    ID:"",
    url:"",
    handleCredential: function(response) {
        console.log("callback called:"+response.credential)
        // decodeJwtResponse() is a custom function defined by you
        // to decode the credential response.
        const responsePayload = jwt_decode(response.credential);

        console.log("ID: " + responsePayload.sub);
        console.log('Full Name: ' + responsePayload.name);
        console.log('Given Name: ' + responsePayload.given_name);
        console.log('Family Name: ' + responsePayload.family_name);
        console.log("Image URL: " + responsePayload.picture);
        console.log("Email: " + responsePayload.email);

        Login.name=responsePayload.name
        Login.email= responsePayload.email
        Login.ID=response.credential
        Login.url= responsePayload.picture
        // external event
        m.redraw()
    }
}

const LoginView = {
    view: function () {
        return m('div', {class: 'container'}, [
            m("div", {
                "id": "g_id_onload",
                "data-client_id": "347549281772-ud7a93hp8e5s72iaktcop31186o62m76.apps.googleusercontent.com",
                "data-callback": "handleCredentialResponse"
            }),
            m("div", {
                "class": "g_id_signin",
                "data-type": "standard"
            }),
        ])
    }
};

function handleCredentialResponse(response) {
    console.log("callback called:"+response.credential)
    Login.handleCredential(response)
    credential = response.credential
}

var Navbar = {
    view: function() {
        return m("nav.navbar.navbar-expand-lg.navbar-light.bg-light", [
            m('a.navbar-brand', { href: '#' }, 'Pétitions'),
            m('button.navbar-toggler', {
                type: 'button',
                'data-toggle': 'collapse',
                'data-target': '#navbarSupportedContent',
                'aria-controls': 'navbarSupportedContent',
                'aria-expanded': 'false',
                'aria-label': 'Toggle navigation'
            }, [
                m('span.navbar-toggler-icon')
            ]),
            m('div.collapse.navbar-collapse.justify-content-end', { id: 'navbarSupportedContent' }, [
                m(LoginView)
            ]),
        ])
    }
}
