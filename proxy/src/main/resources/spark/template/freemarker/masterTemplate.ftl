<#macro masterTemplate title="Home">

<html>

<head>
    <title>${title} | VerifyService</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto+Mono:wght@300&display=swap" rel="stylesheet">
</head>

<body>
    <!-- particles.js container -->
    <div id="particles-js">
        
        <!--
            Fehler  red
            Dein Account wurde nicht verbunden! <br> Du kannst dieses Fenster jetzt schließen.
            -
            Verbunden mit <a style="color: lime;">kexesser</a> rgb(255, 187, 0)
            Du kannst dieses Fenster jetzt schließen.
            -
            Nebelniek Subserver VerifyService rgb(255, 187, 0)
            /verify [discord/twitch]
            -
            404 rgb(255, 187, 0)
            Diese Seite existiert nicht!
         -->
        <div class="footer">
            <p>© 2021 <a href="https://www.nebelniek.de">Nebelniek.de</a></p>
        </div>
    </div>
    <!-- particles.js lib - https://github.com/VincentGarreau/particles.js -->
    <script src="http://cdn.jsdelivr.net/particles.js/2.0.0/particles.min.js"></script> <!-- stats.js lib -->
</body>

<style>
    /* ---- reset ---- */
    * {
        z-index: 1;
    }

    body {
        margin: 0;
        font: normal 75% Arial, Helvetica, sans-serif;
    }

    canvas {
        position: block;
        z-index: 2 !important;
    }

    .message {
        position: fixed;
        color: white;
        font-size: 30px;
        font-family: 'Roboto Mono', monospace;
        text-align: center;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
    }

    .footer {
        position: fixed;
        color: white;
        font-size: 10px;
        font-weight: 100;
        left: 50%;
        transform: translate(-50%, -50%);
        bottom: 10px;
    }

    .footer a {
        color: rgb(255, 239, 184);
        text-decoration: none;
    }

    /* ---- particles.js container ---- */
    #particles-js {
        position: absolute;
        width: 100%;
        height: 100%;
        background-color: #242424;
        background-image: url("");
        background-repeat: no-repeat;
        background-size: cover;
        background-position: 50% 50%;
    }
</style>

<script>
    particlesJS("particles-js", {
        "particles": {
            "number": {
                "value": 400,
                "density": {
                    "enable": true,
                    "value_area": 800
                }
            },
            "color": {
                "value": "#fff"
            },
            "shape": {
                "type": "circle",
                "stroke": {
                    "width": 0,
                    "color": "#000000"
                },
                "polygon": {
                    "nb_sides": 5
                },
                "image": {
                    "src": "img/github.svg",
                    "width": 100,
                    "height": 100
                }
            },
            "opacity": {
                "value": 0.5,
                "random": true,
                "anim": {
                    "enable": false,
                    "speed": 1,
                    "opacity_min": 0.1,
                    "sync": false
                }
            },
            "size": {
                "value": 10,
                "random": true,
                "anim": {
                    "enable": false,
                    "speed": 40,
                    "size_min": 0.1,
                    "sync": false
                }
            },
            "line_linked": {
                "enable": false,
                "distance": 500,
                "color": "#ffffff",
                "opacity": 0.4,
                "width": 2
            },
            "move": {
                "enable": true,
                "speed": 2,
                "direction": "bottom",
                "random": false,
                "straight": false,
                "out_mode": "out",
                "bounce": false,
                "attract": {
                    "enable": false,
                    "rotateX": 600,
                    "rotateY": 1200
                }
            }
        },
        "interactivity": {
            "detect_on": "canvas",
            "events": {
                "onhover": {
                    "enable": false,
                    "mode": "bubble"
                },
                "onclick": {
                    "enable": true,
                    "mode": "remove"
                },
                "resize": true
            },
            "modes": {
                "grab": {
                    "distance": 400,
                    "line_linked": {
                        "opacity": 0.5
                    }
                },
                "bubble": {
                    "distance": 400,
                    "size": 4,
                    "duration": 0.3,
                    "opacity": 1,
                    "speed": 3
                },
                "repulse": {
                    "distance": 200,
                    "duration": 0.4
                },
                "push": {
                    "particles_nb": 4
                },
                "remove": {
                    "particles_nb": 2
                }
            }
        },
        "retina_detect": true
    }); var count_particles, stats, update; stats = new Stats; stats.setMode(0); stats.domElement.style.position = 'absolute'; stats.domElement.style.left = '0px'; stats.domElement.style.top = '0px'; document.body.appendChild(stats.domElement); count_particles = document.querySelector('.js-count-particles'); update = function () { stats.begin(); stats.end(); if (window.pJSDom[0].pJS.particles && window.pJSDom[0].pJS.particles.array) { count_particles.innerText = window.pJSDom[0].pJS.particles.array.length; } requestAnimationFrame(update); }; requestAnimationFrame(update);;
</script>

</html>

</#macro>