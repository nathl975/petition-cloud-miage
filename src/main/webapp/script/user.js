// Asynchronously load the Google Platform library
function loadButton() {
    var script = document.createElement('script');
    script.src = 'https://accounts.google.com/gsi/client';
    script.async = true;
    script.defer = true;
    script.onload = function() {
        // Initialize Google One Tap after the library has loaded
        google.accounts.id.initialize({
            client_id: "347549281772-7ueqjbqnfmd2m05vdsb50p87lua8o6i0.apps.googleusercontent.com",
            callback: handleCredentialResponse
        });
        google.accounts.id.renderButton(
            document.getElementById("google-signin-button"),
            { theme: "outline", size: "large" }  // customize the button
        );
    };
    document.body.appendChild(script);
}

// Handle Google Sign-In response
function handleCredentialResponse(response) {
    var credential = response.credential;
    const responsePayload = jwt_decode(credential);

    // Store the user name in local storage
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
}

var Navbar = {
    view: function() {
        const userName = localStorage.getItem('userName');

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
                userName ? m('div', 'Bonjour, ' + userName) : m('div', { id: 'google-signin-button' })
            ]),
        ])
    }
}

// Load Google Sign-In button and check if user is signed in after the page has loaded
window.onload = function() {
    loadButton();
}
