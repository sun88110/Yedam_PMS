document.addEventListener("DOMContentLoaded", function() {
    const csrfMeta = document.querySelector("meta[name='_csrf']");
    const csrfHeaderMeta = document.querySelector("meta[name='_csrf_header']");
    
    if (csrfMeta && csrfHeaderMeta) {
        window.csrfToken = csrfMeta.getAttribute("content");
        window.csrfHeader = csrfHeaderMeta.getAttribute("content");
    }
});