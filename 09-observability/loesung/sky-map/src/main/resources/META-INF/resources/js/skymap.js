//―――――― CONSTANTS ――――――
const SIZE_FACTOR   = 50;
const KM            = 1_000;
const EARTH_RADIUS  = 6_371 * KM;
const SCALE_FACTOR  = SIZE_FACTOR / EARTH_RADIUS;

//―――――― SETUP SCENE ――――――
const scene    = new THREE.Scene();
const camera   = new THREE.PerspectiveCamera(
    75,
    window.innerWidth / window.innerHeight,
    0.1,
    1000
);
const renderer = new THREE.WebGLRenderer({ antialias: true });
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

//―――――― LIGHTING ――――――
scene.add(new THREE.AmbientLight(0x555555));
const sun = new THREE.DirectionalLight(0xffffff, 1);
sun.position.set(5, 3, 5);
scene.add(sun);

//―――――― EARTH WITH TEXTURE ――――――
const loader = new THREE.TextureLoader();
const earthMap = loader.load('https://threejs.org/examples/textures/land_ocean_ice_cloud_2048.jpg');
const earthBump = loader.load('https://threejs.org/examples/textures/earthbump1k.jpg');
const earthSpec = loader.load('https://threejs.org/examples/textures/earthspec1k.jpg');

const earthGeo = new THREE.SphereGeometry(
    EARTH_RADIUS * SCALE_FACTOR,
    64,
    64
);
const earthMat = new THREE.MeshPhongMaterial({
    map: earthMap,
    bumpMap: earthBump,
    bumpScale: 0.05,
    specularMap: earthSpec,
    specular: 0x222222,
    shininess: 10
});
const earthMesh = new THREE.Mesh(earthGeo, earthMat);
scene.add(earthMesh);

//―――――― ORBIT ――――――
let orbitMesh;

//―――――― SATELLITE ――――――
const satGeo  = new THREE.SphereGeometry(
    50 * KM * SCALE_FACTOR,
    16,
    16
);
const satMat  = new THREE.MeshPhongMaterial({ color: 0xffaa00 });
const satMesh = new THREE.Mesh(satGeo, satMat);
scene.add(satMesh);

camera.position.z = 150;

//―――――― HELPERS ――――――
function sphToCart(latDeg, lonDeg, radius) {
    const φ = THREE.MathUtils.degToRad(latDeg);
    const λ = THREE.MathUtils.degToRad(lonDeg);
    const cosφ = Math.cos(φ);
    return new THREE.Vector3(
        radius * cosφ * Math.cos(λ),
        radius * cosφ * Math.sin(λ),
        radius * Math.sin(φ)
    );
}

let trackId = '25544';

function updateSatellite() {
    const url = `/satellite/${trackId}/pos`;
    $.get(url, result => {
        const info = document.getElementById('info');
        if (info) {
            info.innerText =
                `SAT: ${trackId} — Lat: ${result.latitude.toFixed(3)}°, ` +
                `Lon: ${result.longitude.toFixed(3)}°, ` +
                `Alt: ${result.altitude.toFixed(1)} km`;
        }

        const R   = (EARTH_RADIUS + result.altitude * KM) * SCALE_FACTOR;
        const pos = sphToCart(result.latitude, result.longitude, R);
        satMesh.position.copy(pos);

        if (orbitMesh) {
            scene.remove(orbitMesh);
            orbitMesh.geometry.dispose();
            orbitMesh.material.dispose();
        }

        const segments = 128;
        const pts = [];
        for (let i = 0; i <= segments; i++) {
            const θ = (i / segments) * Math.PI * 2;
            pts.push(new THREE.Vector3(
                R * Math.cos(θ),
                0,
                R * Math.sin(θ)
            ));
        }
        const orbitGeo = new THREE.BufferGeometry().setFromPoints(pts);
        const orbitMat = new THREE.LineBasicMaterial({ color: 0x00ff00 });
        orbitMesh = new THREE.LineLoop(orbitGeo, orbitMat);

        orbitMesh.rotation.x = Math.PI / 2;
        scene.add(orbitMesh);
    });
}

//―――――― FORM HANDLER ――――――
document.getElementById('satForm').addEventListener('submit', e => {
    e.preventDefault();
    const input = document.getElementById('satIdInput').value.trim();
    if (input) {
        trackId = input;
        updateSatellite();
    }
});

//―――――― ANIMATION & POLLING ――――――
updateSatellite();
setInterval(updateSatellite, 5000);

function animate() {
    requestAnimationFrame(animate);
    earthMesh.rotation.y += 0.00005;
    renderer.render(scene, camera);
}
animate();

window.addEventListener('resize', () => {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
});
