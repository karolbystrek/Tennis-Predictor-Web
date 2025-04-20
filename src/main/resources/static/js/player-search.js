document.addEventListener('DOMContentLoaded', () => {
    const CACHE_KEY = 'playersDataCache';
    const CACHE_DURATION_MS = 1000 * 60 * 60;

    const modalElement = document.getElementById('playerSearchModal');
    const searchInputElement = document.getElementById('playerSearchInput');
    const searchResultsContainer = document.getElementById('searchResults');
    const playerSearchTriggers = document.querySelectorAll('.player-search-trigger');
    const closeModalButton = modalElement.querySelector('.close');
    const predictionForm = document.getElementById('prediction-form');

    let playersCache = [];
    let activePlayerFieldId = null;

    initializeEventListeners();
    loadPlayers();

    /**
     * Sets up all necessary event listeners for the UI elements.
     */
    function initializeEventListeners() {
        playerSearchTriggers.forEach(trigger => {
            trigger.addEventListener('click', handleTriggerClick);
        });

        closeModalButton.addEventListener('click', closeModal);
        window.addEventListener('click', handleOutsideModalClick);

        searchInputElement.addEventListener('input', handleSearchInput);

        if (predictionForm) {
            predictionForm.addEventListener('submit', handleFormSubmit);
        } else {
            console.warn('Prediction form not found. Submission validation skipped.');
        }
    }

    /**
     * Handles clicking on a player search trigger input.
     * Sets the active field and opens the modal.
     * @param {Event} event - The click event object.
     */
    function handleTriggerClick(event) {
        activePlayerFieldId = event.target.dataset.player;
        if (!activePlayerFieldId) {
            console.error('Player trigger clicked without a data-player attribute.');
            return;
        }
        openModal();
    }

    /**
     * Opens the player search modal, resets search, and displays players.
     */
    function openModal() {
        searchInputElement.value = '';
        renderPlayerList(playersCache);
        modalElement.style.display = 'block';
        searchInputElement.focus();
    }

    /**
     * Closes the player search modal.
     */
    function closeModal() {
        modalElement.style.display = 'none';
        activePlayerFieldId = null;
    }

    /**
     * Closes the modal if a click occurs outside the modal content area.
     * @param {MouseEvent} event - The click event object.
     */
    function handleOutsideModalClick(event) {
        if (event.target === modalElement) {
            closeModal();
        }
    }

    /**
     * Handles input events on the search field to filter the player list.
     * @param {InputEvent} event - The input event object.
     */
    function handleSearchInput(event) {
        const searchTerm = event.target.value.toLowerCase().trim();
        const filteredPlayers = playersCache.filter(player =>
            player.name.toLowerCase().includes(searchTerm)
        );
        renderPlayerList(filteredPlayers);
    }

    /**
     * Validates the prediction form before submission.
     * Ensures both players are selected and are different.
     * @param {Event} event - The submit event object.
     */
    function handleFormSubmit(event) {
        const player1Id = document.getElementById('player1Id')?.value;
        const player2Id = document.getElementById('player2Id')?.value;

        let isValid = true;
        let alertMessage = '';

        if (!player1Id || !player2Id) {
            alertMessage = 'Please select both players.';
            isValid = false;
        } else if (player1Id === player2Id) {
            alertMessage = 'Please select different players for the match.';
            isValid = false;
        }

        if (!isValid) {
            event.preventDefault();
            alert(alertMessage);
        }
    }

    /**
     * Loads player data, first trying the cache, then fetching from the server.
     * @async
     */
    async function loadPlayers() {
        try {
            const cached = getFromCache(CACHE_KEY);
            if (cached) {
                playersCache = cached;
                console.log('Loaded players from cache.');
                return;
            }

            console.log('Fetching players from server...');
            const response = await fetch('/players');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const rawPlayers = await response.json();

            playersCache = rawPlayers.map(p => ({
                id: p.playerId,
                name: `${p.firstName} ${p.lastName}`,
                country: p.ioc
            }));

            saveToCache(CACHE_KEY, playersCache, CACHE_DURATION_MS);
            console.log('Players fetched and cached.');

        } catch (error) {
            console.error('Error loading players:', error);
            searchResultsContainer.innerHTML = '<div class="error">Failed to load player data. Please try again later.</div>';
        }
    }

    /**
     * Renders a list of players in the search results container.
     * @param {Array<object>} playersToDisplay - Array of player objects to display.
     */
    function renderPlayerList(playersToDisplay) {
        searchResultsContainer.innerHTML = '';

        if (!playersToDisplay || playersToDisplay.length === 0) {
            searchResultsContainer.innerHTML = '<div class="no-results">No players found.</div>';
            return;
        }

        const fragment = document.createDocumentFragment();
        playersToDisplay.forEach(player => {
            const playerItem = document.createElement('div');
            playerItem.className = 'player-item';
            playerItem.dataset.playerId = player.id;
            playerItem.innerHTML = `<strong>${escapeHtml(player.name)}</strong> (${escapeHtml(player.country)})`;

            playerItem.addEventListener('click', () => handlePlayerSelection(player));
            fragment.appendChild(playerItem);
        });
        searchResultsContainer.appendChild(fragment);
    }

    /**
     * Handles the selection of a player from the modal list.
     * Updates the corresponding input fields and closes the modal if valid.
     * @param {object} selectedPlayer - The player object that was clicked.
     */
    function handlePlayerSelection(selectedPlayer) {
        if (!activePlayerFieldId) return;

        const targetNameInput = document.getElementById(`player${activePlayerFieldId}Name`);
        const targetIdInput = document.getElementById(`player${activePlayerFieldId}Id`);

        const otherPlayerFieldId = activePlayerFieldId === '1' ? '2' : '1';
        const otherPlayerIdInput = document.getElementById(`player${otherPlayerFieldId}Id`);
        const otherPlayerSelectedId = otherPlayerIdInput ? otherPlayerIdInput.value : null;

        const selectedPlayerIdStr = String(selectedPlayer.id);
        const otherPlayerSelectedIdStr = String(otherPlayerSelectedId);

        if (otherPlayerSelectedId && otherPlayerSelectedId !== "" && selectedPlayerIdStr === otherPlayerSelectedIdStr) {
            alert('Please select different players for each field.');
            return;
        }

        if (targetNameInput && targetIdInput) {
            targetNameInput.value = selectedPlayer.name;
            targetIdInput.value = selectedPlayerIdStr;
        } else {
            console.error(`Input fields for player ${activePlayerFieldId} not found.`);
        }

        closeModal();
    }

    /**
     * Saves data to local storage with an expiry timestamp.
     * @param {string} key - The key to store the data under.
     * @param {any} data - The data to store.
     * @param {number} durationMs - The duration in milliseconds until the data expires.
     */
    function saveToCache(key, data, durationMs) {
        const item = {
            data: data,
            expiry: Date.now() + durationMs,
        };
        try {
            localStorage.setItem(key, JSON.stringify(item));
        } catch (e) {
            console.error('Error saving to localStorage', e);
        }
    }

    /**
     * Retrieves data from local storage if it exists and has not expired.
     * @param {string} key - The key to retrieve the data for.
     * @returns {any|null} The cached data, or null if not found or expired.
     */
    function getFromCache(key) {
        try {
            const itemStr = localStorage.getItem(key);
            if (!itemStr) {
                return null;
            }
            const item = JSON.parse(itemStr);
            const now = Date.now();
            if (now > item.expiry) {
                localStorage.removeItem(key);
                return null;
            }
            return item.data;
        } catch (e) {
            console.error('Error reading from localStorage', e);
            return null;
        }
    }

    /**
     * Basic HTML escaping function to prevent XSS.
     * @param {string} unsafe - The string to escape.
     * @returns {string} The escaped string.
     */
    function escapeHtml(unsafe) {
        if (unsafe === null || unsafe === undefined) return '';
        return String(unsafe)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

});
