// Asynchronously load the Google Platform library
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

function handleSignOut() {
    localStorage.removeItem('userName');

    location.reload();
}

function handleCredentialResponse(response) {
    var credential = response.credential;
    const responsePayload = jwt_decode(credential);

    localStorage.setItem('userName', responsePayload.name);

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
        .then(data => console.log('User data sent to backend: ', data))
        .catch((error) => {
            console.error('Error:', error);
        });
    location.reload();

}

var Navbar = {
    view: function() {
        const userName = localStorage.getItem('userName');
        const userId = localStorage.getItem('userId');

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
            m('div.collapse.navbar-collapse.justify-content-end', { id: 'loadButton' }, [
                userName ? m('div', 'Bonjour, ' + userName, [
                    m('button.btn.btn-outline-secondary', { onclick: handleSignOut }, 'Déconnexion')
                ]) : m('div', { id: 'google-signin-button' })
            ]),
        ])
    }
}

window.onload = function() {
    loadButton();
}
