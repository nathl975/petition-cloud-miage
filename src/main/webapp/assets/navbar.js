function loadButton() {
    var script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = function() {
        // Initialize Google One Tap after the library has loaded
        google.accounts.id.initialize({
            client_id: "347549281772-ud7a93hp8e5s72iaktcop31186o62m76.apps.googleusercontent.com",
            callback: handleCredentialResponse
        });
        google.accounts.id.renderButton(
            document.getElementById("google-signin-button"),
            { theme: "outline", size: "large" }  // customize the button
        );
    };
    document.body.appendChild(script);
}


function handleCredentialResponse(response) {
    var credential = response.credential;
    const responsePayload = jwt_decode(credential);

    // Send a POST request to the backend with user data
    fetch('/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            userId: responsePayload.sub,
            name: responsePayload.name,
        }),
    })
        .then(response => response.json())
        .then(data => {
            // Update JWT and user info
            localStorage.setItem('jwt', data.jwt);
            localStorage.setItem('userName', data.user.name);
        })
        .catch((error) => {
            console.error('Error:', error);
        });

}

function handleSignOut() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('userName');
    location.reload();
}


function loadButton() {
    var script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = function() {
        google.accounts.id.initialize({
            client_id: "347549281772-ud7a93hp8e5s72iaktcop31186o62m76.apps.googleusercontent.com",
            callback: handleCredentialResponse
        });
        google.accounts.id.renderButton(
            document.getElementById("google-signin-button"),
            { theme: "outline", size: "large" }
        );
    };
    document.body.appendChild(script);
}


function handleCredentialResponse(response) {
    var credential = response.credential;
    const responsePayload = jwt_decode(credential);

    fetch('/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            userId: responsePayload.sub,
            name: responsePayload.name,
        }),
    })
        .then(response => response.json())
        .then(data => {
            // Update JWT and user info
            localStorage.setItem('jwt', data.jwt);
            localStorage.setItem('userName', data.user.name);

            // Reload page
            window.location.reload();
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}



function handleSignOut() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('userName');
    location.reload();
}


var Navbar = {
    view: function() {
        const jwtToken = localStorage.getItem('jwt');
        const userName = localStorage.getItem('userName');
        return m("nav.navbar.navbar-expand-lg.navbar-dark.bg-dark", [
            m('a.navbar-brand', { href: '/' }, 'Accueil'),
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
            m('div.collapse.navbar-collapse', {id: 'navbarSupportedContent'}, [
                m('ul.navbar-nav.mr-auto', [
                    jwtToken ? m('li.nav-item', [ // Si un jeton JWT existe, afficher le lien "Mes pétitions signées"
                        m('a.navbar-brand', { href: '/my-petitions' }, 'Mes pétitions signées'),
                    ]) : null, // Si aucun jeton JWT n'existe, ne pas afficher le lien
                    jwtToken ? m('li.nav-item', [ // Si un jeton JWT existe, afficher le lien "Créer une pétition"
                        m('a.navbar-brand', { href: '/petition-create' }, 'Créer une pétition'),
                    ]) : null // Si aucun jeton JWT n'existe, ne pas afficher le lien
                ]),
                m('div.collapse.navbar-collapse.justify-content-end', { id: 'loadButton' }, [
                    userName ? [
                            m('span.navbar-text', {style: 'margin-right: 10px;'}, [
                                'Bonjour, ' + userName
                            ]),
                            m('button.btn.btn-outline-danger', { onclick: handleSignOut }, 'Déconnexion')
                        ]
                        : m('div', { id: 'google-signin-button' })
                ]),
            ]),
        ])
    }
}


window.onload = function() {
    loadButton();
}
