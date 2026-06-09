async function loadContent(fetchUrl, browserUrl = fetchUrl, push = true) {
    const response = await fetch(fetchUrl);

    if (!response.ok) {
        console.error("Không load được trang:", fetchUrl);
        return;
    }

    const html = await response.text();

    document.getElementById("content").innerHTML = html;
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });

    // Clear ô tìm kiếm
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.value = "";
    }

    if (push) {
        history.pushState(
            { fetchUrl: fetchUrl },
            null,
            browserUrl
        );
    }
}

history.replaceState(
    { fetchUrl: "/home-content" },
    null,
    "/Home"
);
window.addEventListener("popstate", (event) => {
    const fetchUrl = event.state?.fetchUrl;

    if (!fetchUrl) return;

    loadContent(fetchUrl, location.pathname, false);
});