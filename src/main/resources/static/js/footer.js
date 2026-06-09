const token = window.SPOTIFY_TOKEN;

let player;
let deviceId;
let currentState = null;

function formatTime(ms) {
    const s = Math.floor(ms / 1000);
    return Math.floor(s / 60) + ":" + String(s % 60).padStart(2, "0");
}

let isAutoNexting = false;
let repeatMode = false;
const repeatBtn = document.getElementById("repeat-btn");

repeatBtn.addEventListener("click", () => {
    repeatMode = !repeatMode;

    repeatBtn.classList.toggle("active", repeatMode);

    console.log("Repeat:", repeatMode);
});
async function replayCurrentTrack() {
    if (!currentState) return;

    await player.seek(0);

    await player.resume();
}
async function updateFooter(state) {
    if (!state) return;

    currentState = state;

    const track = state.track_window.current_track;

    document.getElementById("footer-title").innerText = track.name;
    document.getElementById("footer-artist").innerText =
        track.artists.map(a => a.name).join(", ");

    if (track.album.images.length > 0) {
        document.getElementById("footer-cover").src = track.album.images[0].url;
    }

    document.getElementById("current-time").innerText = formatTime(state.position);
    document.getElementById("duration").innerText = formatTime(state.duration);

    const progressBar = document.getElementById("progress-bar");
    progressBar.max = state.duration;
    progressBar.value = state.position;

    document.getElementById("play-btn").innerText =
        state.paused ? "▶" : "⏸";

    if (
        !state.paused &&
        !isAutoNexting &&
        state.position >= state.duration - 1000
    ) {
        isAutoNexting = true;

        if (repeatMode) {
            await replayCurrentTrack();
        } else {
            await nextTrack();
        }

        setTimeout(() => {
            isAutoNexting = false;
        }, 2000);
    }
}

window.onSpotifyWebPlaybackSDKReady = () => {
    player = new Spotify.Player({
        name: "Spotiflie Web Player",
        getOAuthToken: cb => cb(token),
        volume: 0.5
    });
    player.addListener("ready", ({ device_id }) => {
        console.log("Web Player ready:", device_id);
        deviceId = device_id;
        window.SPOTIFY_DEVICE_ID = device_id;

        const status = document.getElementById("player-status");
        if (status) status.innerText = "Web Player ready";
    });

    player.addListener("not_ready", ({ device_id }) => {
        console.log("Device offline:", device_id);
    });
    player.addListener("account_error", ({ message }) => {
        console.error("Account error:", message);
        alert("Tài khoản cần Spotify Premium");
    });
    player.connect();
};

window.playTrack = async function (id) {
    console.log("Click track:", id);
    console.log("deviceId:", deviceId);
    const uri = `spotify:track:${id}`;

    if (!deviceId) {
        alert("Web Player chưa sẵn sàng, chờ vài giây rồi bấm lại");
        return;
    }

    const response = await fetch("/spotify/play-web?deviceId=" + deviceId, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ uri: uri })
    });

    console.log(await response.text());
};

window.togglePlay = function () {
    if (player) {
        player.togglePlay();
    }
};

setInterval(() => {
    if (!player) return;

    player.getCurrentState().then(state => {
        if (state) {
            updateFooter(state);
        }
    });
}, 1000);

document.addEventListener("DOMContentLoaded", () => {
    const progressBar = document.getElementById("progress-bar");
    const volumeBar = document.getElementById("volume-bar");

    if (progressBar) {
        progressBar.addEventListener("change", function () {
            if (player) {
                player.seek(Number(this.value));
            }
        });
    }

    if (volumeBar) {
        volumeBar.addEventListener("input", function () {
            if (player) {
                player.setVolume(this.value / 100);
            }
        });
    }
});

let currentTopTrackIndex = 0;
window.playTrackByIndex = async function(element, index) {
    currentTopTrackIndex = index;
    const trackId = element.dataset.trackId;
    console.log("Play index:", currentTopTrackIndex);
    console.log("Play id:", trackId);

    if (!trackId) return;

    await playTrack(trackId);
};

window.nextTrack = async function() {
    currentTopTrackIndex++;

    if (currentTopTrackIndex >= window.topTrackIds.length) {
        currentTopTrackIndex = 0;
    }

    const id = window.topTrackIds[currentTopTrackIndex];

    console.log("Next index:", currentTopTrackIndex);
    console.log("Next id:", id);

    if (!id) return;

    await playTrack(id);
};

window.previousTrack = async function() {

    currentTopTrackIndex--;

    if (currentTopTrackIndex < 0) {
        currentTopTrackIndex = window.topTrackIds.length - 1;
    }

    const id = window.topTrackIds[currentTopTrackIndex];

    console.log("Previous index:", currentTopTrackIndex);
    console.log("Previous id:", id);

    if (!id) return;

    await playTrack(id);
};
document.addEventListener("keydown", async (e) => {

    const tag = document.activeElement.tagName;

    if (
        tag === "INPUT" ||
        tag === "TEXTAREA"
    ) {
        return;
    }

    if (e.code === "Space") {
        e.preventDefault();
        player.togglePlay();
    }

    // Mũi tên phải: tua tới 10 giây
    if (e.code === "ArrowRight") {
        e.preventDefault();

        const state = await player.getCurrentState();
        if (!state) return;

        await player.seek(
            Math.min(state.position + 10000, state.duration)
        );
    }

    // Mũi tên trái: lùi 10 giây
    if (e.code === "ArrowLeft") {
        e.preventDefault();

        const state = await player.getCurrentState();
        if (!state) return;

        await player.seek(
            Math.max(state.position - 10000, 0)
        );
    }
});

