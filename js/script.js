let menuVisible = false;

function mostrarOcultarMenu() {
    const nav = document.getElementById("nav");
    if (menuVisible) {
        nav.classList.remove("responsive"); 
        menuVisible = false;
    } else {
        nav.classList.add("responsive");
        menuVisible = true;
    }
}

function seleccionar(){
    document.getElementById("nav").classList = "";
    menuVisible = false;
}

const categorias = document.querySelectorAll('.categoria');

//Seleccionar Categorias
categorias.forEach((categoria) => {
    categoria.addEventListener('click', () => {
        categorias.forEach((c) => {
            c.style.opacity = 0.6; 
            c.querySelector('.categoria-contenido').style.color = '#ffffff'; 
        });

        categoria.style.opacity = 1; 
        categoria.querySelector('.categoria-contenido').style.color = '#ffffff'; 
    });
});

//Filtrador
const searchInput = document.getElementById('searchInput');
const trajeRows = document.querySelectorAll('tbody tr');

searchInput.addEventListener('input', function () {
    const searchTerm = searchInput.value.toLowerCase();

    trajeRows.forEach((row) => {
        const nombreTraje = row.querySelector('td:nth-child(2)').textContent.toLowerCase();
        if (nombreTraje.includes(searchTerm)) {
            row.style.display = '';
        } else {
            row.style.display = 'none';
        }
    });
});

//Mostrar tallas
const viewButtons = document.querySelectorAll('.view');
const tallaInfoContainers = document.querySelectorAll('.talla-info');
viewButtons.forEach((button, index) => {
    button.addEventListener('click', () => {
        tallaInfoContainers[index].style.display = 'block';
    });
});

for (let i = 1; i <= tallaInfoContainers.length; i++) {
    const closeBtn = document.querySelector(`.close-talla-info-${i}`);
    closeBtn.addEventListener('click', () => {
        tallaInfoContainers[i - 1].style.display = 'none';
    });
}






