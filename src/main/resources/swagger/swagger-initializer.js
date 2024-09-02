// Custom Plugin to add a search bar and filter endpoints with additional options and improved styling
const SearchPlugin = () => {
  return {
    // Inserting the search bar after initialising Swagger-UI
    afterLoad: function(system) {
      // Wait for the DOM to load and then add the search field
      const observer = new MutationObserver(() => {
        const container = document.querySelector("#swagger-ui > section > div.swagger-ui > div:nth-child(2) > div:nth-child(4)");

        if (container && !document.getElementById('endpoint-search')) {
          // Creating the search field and filter options
          const searchBar = document.createElement("div");
          searchBar.id = "search-container";
          searchBar.style.display = "flex";  // Use flexbox for better alignment
          searchBar.style.alignItems = "center"; // Center align items vertically
          searchBar.style.marginBottom = "20px";

          const searchInput = document.createElement("input");
          searchInput.type = "text";
          searchInput.id = "endpoint-search";
          searchInput.placeholder = "Search Endpoints...";
          searchInput.style.flex = "1";
          searchInput.style.padding = "8px";
          searchInput.onkeydown = filterEndpoints;
          searchInput.onkeyup = filterEndpoints;

          // Create case sensitivity toggle (button)
          const caseSensitiveButton = document.createElement("button");
          caseSensitiveButton.id = "case-sensitive";
          caseSensitiveButton.style.marginLeft = "10px";
          caseSensitiveButton.style.padding = "8px";
          caseSensitiveButton.style.border = "1px solid #ccc";
          caseSensitiveButton.style.backgroundColor = "#f5f5f5";
          caseSensitiveButton.style.cursor = "pointer";
          caseSensitiveButton.innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\"><path fill=\"currentColor\" d=\"M8.854 11.702h-1l-.816-2.159H3.772l-.768 2.16H2L4.954 4h.935zm-2.111-2.97L5.534 5.45a3 3 0 0 1-.118-.515h-.021q-.054.327-.124.515L4.073 8.732zm7.013 2.97h-.88v-.86h-.022q-.574.99-1.692.99q-.822 0-1.29-.436q-.46-.435-.461-1.155q0-1.54 1.815-1.794l1.65-.23q0-1.403-1.134-1.403q-.994 0-1.794.677V6.59q.81-.516 1.87-.516q1.938 0 1.938 2.052zm-.88-2.782l-1.327.183q-.614.086-.924.306q-.312.215-.312.768q0 .403.285.66q.29.253.768.253a1.41 1.41 0 0 0 1.08-.457q.43-.462.43-1.165z\"/></svg>"; // Icon for case sensitivity
          caseSensitiveButton.title = "Toggle case sensitivity"; // Tooltip text
          caseSensitiveButton.onclick = toggleCaseSensitive;

          // Create description search toggle (button)
          const searchByDescriptionButton = document.createElement("button");
          searchByDescriptionButton.id = "search-description";
          searchByDescriptionButton.style.marginLeft = "10px";
          searchByDescriptionButton.style.padding = "8px";
          searchByDescriptionButton.style.border = "1px solid #ccc";
          searchByDescriptionButton.style.backgroundColor = "#f5f5f5";
          searchByDescriptionButton.style.cursor = "pointer";
          searchByDescriptionButton.innerHTML = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1em\" height=\"1em\" viewBox=\"0 0 16 16\"><path fill=\"currentColor\" d=\"M1.5 3a.5.5 0 0 0 0 1h13a.5.5 0 0 0 0-1zm0 3a.5.5 0 0 0 0 1h13a.5.5 0 0 0 0-1zM1 9.5a.5.5 0 0 1 .5-.5h13a.5.5 0 0 1 0 1h-13a.5.5 0 0 1-.5-.5m.5 2.5a.5.5 0 0 0 0 1h9a.5.5 0 0 0 0-1z\"/></svg>"; // Icon for searching in descriptions
          searchByDescriptionButton.title = "Toggle search in descriptions"; // Tooltip text
          searchByDescriptionButton.onclick = toggleSearchDescription;

          searchBar.appendChild(searchInput);
          searchBar.appendChild(caseSensitiveButton);
          searchBar.appendChild(searchByDescriptionButton);

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

// Toggle case sensitivity
function toggleCaseSensitive() {
  const caseSensitiveButton = document.getElementById('case-sensitive');
  caseSensitiveButton.classList.toggle('active');
  caseSensitiveButton.style.backgroundColor = caseSensitiveButton.classList.contains('active') ? '#007bff' : '#f5f5f5';
  caseSensitiveButton.style.color = caseSensitiveButton.classList.contains('active') ? '#fff' : '#000';
  filterEndpoints();
}

// Toggle search in descriptions
function toggleSearchDescription() {
  const searchByDescriptionButton = document.getElementById('search-description');
  searchByDescriptionButton.classList.toggle('active');
  searchByDescriptionButton.style.backgroundColor = searchByDescriptionButton.classList.contains('active') ? '#007bff' : '#f5f5f5';
  searchByDescriptionButton.style.color = searchByDescriptionButton.classList.contains('active') ? '#fff' : '#000';
  filterEndpoints();
}

// Function to filter the endpoints based on the search query and selected options
function filterEndpoints() {
  const input = document.getElementById('endpoint-search').value;
  const caseSensitive = document.getElementById('case-sensitive').classList.contains('active');
  const searchInDescription = document.getElementById('search-description').classList.contains('active');

  const operations = document.querySelectorAll('.opblock');

  operations.forEach((operation) => {
    const summary = operation.querySelector('.opblock-summary-description');
    const endpoint = operation.querySelector('.opblock-summary-path a');
    let textToSearch = endpoint.textContent;

    if (searchInDescription) {
      textToSearch += " " + summary.textContent;
    }

    if (!caseSensitive) {
      textToSearch = textToSearch.toLowerCase();
    }

    const searchValue = caseSensitive ? input : input.toLowerCase();

    if (textToSearch.includes(searchValue)) {
      operation.style.display = '';
    } else {
      operation.style.display = 'none';
    }
  });
}

const EndpointInformationPlugin = (endpointElementId, htmlText) => {
  return {
    afterLoad: function(system) {
      const observer = new MutationObserver(() => {
        const container = document.getElementById(endpointElementId);

        if (container && !document.getElementById('maintenance-info')) {
          // Create a maintenance information div
          const maintenanceInfo = document.createElement("div");
          maintenanceInfo.id = "maintenance-info";
          maintenanceInfo.style.fontSize = "12px";
          maintenanceInfo.style.color = "#555";
          maintenanceInfo.style.textAlign = "left";
          maintenanceInfo.style.marginTop = "8px";  // Add margin to position it below the header
          maintenanceInfo.style.width = "100%";     // Ensure it takes full width

          const infoText = document.createElement("p");
          infoText.style.margin = "0";
          infoText.innerHTML = htmlText;

          maintenanceInfo.appendChild(infoText);

          // Insert the maintenance information right after the first child of the header
          const headerTitle = container.querySelector('span');
          headerTitle.parentNode.insertBefore(maintenanceInfo, headerTitle.nextSibling);

          observer.disconnect();
        }
      });

      observer.observe(document.body, { childList: true, subtree: true });
    }
  };
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
      SearchPlugin,
      EndpointInformationPlugin("operations-tag-Maintenance", "These API Endpoints depend on the Maintenance plugin. If you don't have the plugin installed, you won't be able to access these endpoints. You can install it <a href='https://www.spigotmc.org/resources/maintenance-bungee-and-spigot-support.40699/'>here</a>.")
    ],
    layout: "StandaloneLayout",
  });
};
