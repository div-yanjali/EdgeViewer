const img = document.getElementById("img") as HTMLImageElement;
const stats = document.getElementById("stats") as HTMLElement;

img.onload = () => {
    stats.textContent = `Loaded | ${img.naturalWidth} x ${img.naturalHeight}`;
};
