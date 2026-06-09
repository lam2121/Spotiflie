const avatarBtn =
    document.getElementById("avatarBtn");

const dropdown =
    document.getElementById("profileDropdown");

if (avatarBtn) {

    avatarBtn.addEventListener("click", function (e) {

        e.stopPropagation();

        dropdown.classList.toggle("show");
    });

    document.addEventListener("click", function () {

        dropdown.classList.remove("show");
    });
}
const searchInput = document.getElementById("searchInput");
const searchBtn = document.getElementById("searchBtn");
const searchResults = document.getElementById("searchResults");
searchInput.addEventListener("focus", async () => {
    await loadContent('/home-content');
});
searchBtn.addEventListener("click", searchTracks);

searchInput.addEventListener("keydown",function (e) {
    if (e.key === "Enter") {
        searchTracks();
    }
});

async function searchTracks() {
    const keyword = searchInput.value.trim();
    const searchResults = document.getElementById("searchResults");
    try {
        const url = new URL("https://api.spotify.com/v1/search");

        url.searchParams.set("q", keyword);
        url.searchParams.set("type", "track");
        url.searchParams.set("limit", "10");

        console.log("URL:", url.toString());

        const response = await fetch(url.toString(), {
            method: "GET",
            headers: {
                Authorization: `Bearer ${window.SPOTIFY_TOKEN}`
            }
        });

        const data = await response.json();

        console.log("Status:", response.status);
        console.log("Spotify response:", data);

        renderSearchResults(data.tracks.items);

    } catch (error) {
        console.error("Search error:", error);
        searchResults.innerHTML = "<p>Lỗi khi tìm kiếm.</p>";
    }
}
function renderSearchResults(tracks) {
    const searchResults = document.getElementById("searchResults");
    searchResults.innerHTML = "";

    const section = document.createElement("section");
    section.className = "music-section";

    const grid = document.createElement("div");
    grid.className = "card-grid";

    tracks.forEach(track => {
        const image = track.album.images[0]?.url || "";
        const artists = track.artists.map(a => a.name).join(", ");

        const card = document.createElement("div");
        card.className = "music-card";

        card.innerHTML = `
            <img src="${image}" alt="Track">
            <h3>${track.name}</h3>
            <p>${artists}</p>
        `;

        card.addEventListener("click", () => {
            playSearchedTrack(track.id);
        });

        grid.appendChild(card);
    });

    section.appendChild(grid);
    searchResults.appendChild(section);
}
window.playSearchedTrack = function(id) {
    playTrack(id);
};
let searchTimer;

searchInput.addEventListener("input", () => {
    clearTimeout(searchTimer);

    searchTimer = setTimeout(() => {
        searchTracks();
    }, 500);
});