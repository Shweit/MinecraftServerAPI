const SearchPlugin = () => {
  return {
    // Inserting the search bar after initialising Swagger-UI
    afterLoad: function(system) {
      // Wait for the DOM to load and then add the search field
      const observer = new MutationObserver(() => {
        const container = document.querySelector("#swagger-ui > section > div.swagger-ui > div:nth-child(2) > div:nth-child(4)");

        if (container && !document.getElementById('endpoint-search')) {
          // Creating the search field
          const searchBar = document.createElement("div");
          searchBar.id = "search-container";
          searchBar.style.marginBottom = "20px";

          const searchInput = document.createElement("input");
          searchInput.type = "text";
          searchInput.id = "endpoint-search";
          searchInput.placeholder = "Search Endpoints...";
          searchInput.style.width = "100%";
          searchInput.style.padding = "8px";
          searchInput.onkeydown = filterEndpoints;
          searchInput.onkeyup = filterEndpoints;

          searchBar.appendChild(searchInput);

          // Inserting the search field into the DOM
          container.prepend(searchBar);

          // Stops the observer when the search field has been successfully added
          observer.disconnect();
        }
      });

      // Start the observer to track changes in the DOM
      observer.observe(document.body, { childList: true, subtree: true });
    }
  };
};

// Function for filtering the endpoints based on the search query
function filterEndpoints() {
  const input = document.getElementById('endpoint-search').value.toLowerCase();
  const operations = document.querySelectorAll('.opblock');

  operations.forEach((operation) => {
    const summary = operation.querySelector('.opblock-summary-description');
    if (summary && summary.textContent.toLowerCase().includes(input)) {
      operation.style.display = '';
    } else {
      operation.style.display = 'none';
    }
  });
}

window.onload = function() {
  window.ui = SwaggerUIBundle({
    url: "/api-docs",
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl,
      SearchPlugin
    ],
    layout: "StandaloneLayout",
  });
};
