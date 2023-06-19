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

    m.redraw();
}

function handleSignOut() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('userName');
    location.reload();
}


var Navbar = {
    view: function() {
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
                    m('li.nav-item', [
                        m('a.navbar-brand', { href: '/my-petitions' }, 'Mes pétitions'),
                    ]),
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