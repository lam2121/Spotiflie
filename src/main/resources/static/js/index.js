
window.renderArtists = async function (element) {
    const artistId = element.dataset.artistId;
    await loadContent(`/artist/${artistId}`);
}
window.loadAlbumTracks = async function (element) {
    const albumId = element.dataset.albumId;
    await loadContent(`/album/${albumId}/tracks`);
}
window.playAlbumTrack = async function (element){
    const trackId=element.dataset.trackId;
    await playTrack(trackId);
}