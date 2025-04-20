document.addEventListener('DOMContentLoaded', function () {
    initializeCustomSelects();
});

/**
 * Initialize all custom select elements
 */
function initializeCustomSelects() {
    const customSelects = document.querySelectorAll('.custom-select');

    customSelects.forEach(select => {
        createCustomSelectUI(select);
    });
}

/**
 * Create custom select UI elements for a select element
 * @param {HTMLElement} select - The select container element
 */
function createCustomSelectUI(select) {
    const nativeSelect = select.querySelector('select');

    // Create selected option display
    const selectedOption = document.createElement('div');
    selectedOption.className = 'selected-option';

    // Get label text and setup initial text
    const labelText = select.closest('.form-group').querySelector('label').textContent;
    setupSelectedOptionText(selectedOption, nativeSelect);

    // Create options dropdown
    const optionsList = createOptionsListElement(nativeSelect);

    // Create wrapper and append elements
    const customSelectWrapper = document.createElement('div');
    customSelectWrapper.className = 'custom-select-wrapper';

    nativeSelect.style.display = 'none';

    customSelectWrapper.appendChild(selectedOption);
    customSelectWrapper.appendChild(optionsList);
    select.appendChild(customSelectWrapper);

    // Setup event handlers
    setupEventHandlers(selectedOption, optionsList, customSelectWrapper, nativeSelect);
}

/**
 * Set the appropriate text for the selected option element
 * @param {HTMLElement} selectedOption - The selected option display element
 * @param {HTMLElement} nativeSelect - The native select element
 */
function setupSelectedOptionText(selectedOption, nativeSelect) {
    // Check if there's a selected option that's not disabled
    const hasSelectedValue = Array.from(nativeSelect.options).some(opt =>
        opt.selected && !opt.disabled && opt.value
    );

    if (hasSelectedValue) {
        selectedOption.textContent = nativeSelect.options[nativeSelect.selectedIndex].textContent;
    } else {
        selectedOption.textContent = 'Select...';
        selectedOption.classList.add('placeholder');
    }
}

/**
 * Create the dropdown options list
 * @param {HTMLElement} nativeSelect - The native select element
 * @returns {HTMLElement} The created options list element
 */
function createOptionsListElement(nativeSelect) {
    const optionsList = document.createElement('div');
    optionsList.className = 'options-list';

    // Don't include the disabled placeholder option in the dropdown
    Array.from(nativeSelect.options).forEach((option, index) => {
        // Skip disabled placeholder options
        if (option.disabled && option.value === "") {
            return;
        }

        const optionElement = document.createElement('div');
        optionElement.className = 'option';
        optionElement.dataset.value = option.value;
        optionElement.textContent = option.textContent;

        if (option.disabled) optionElement.classList.add('disabled');
        if (option.selected) optionElement.classList.add('selected');

        optionElement.addEventListener('click', () => {
            if (!option.disabled) {
                updateSelectValue(nativeSelect, option.value, optionElement, optionsList, optionElement.textContent);
            }
        });

        optionsList.appendChild(optionElement);
    });

    return optionsList;
}

/**
 * Setup event handlers for the custom select
 * @param {HTMLElement} selectedOption - The selected option display element
 * @param {HTMLElement} optionsList - The options list dropdown element
 * @param {HTMLElement} customSelectWrapper - The wrapper element
 * @param {HTMLElement} nativeSelect - The native select element
 */
function setupEventHandlers(selectedOption, optionsList, customSelectWrapper, nativeSelect) {
    // Toggle dropdown on click
    selectedOption.addEventListener('click', () => {
        selectedOption.classList.toggle('active');
        optionsList.classList.toggle('show');
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', (e) => {
        if (!customSelectWrapper.contains(e.target)) {
            optionsList.classList.remove('show');
            selectedOption.classList.remove('active');
        }
    });

    // Update UI when native select changes
    nativeSelect.addEventListener('change', () => {
        const selectedIndex = nativeSelect.selectedIndex;
        const selectedText = nativeSelect.options[selectedIndex]?.textContent || 'Select...';

        if (nativeSelect.value) {
            selectedOption.textContent = selectedText;
            selectedOption.classList.remove('placeholder');
        } else {
            selectedOption.textContent = 'Select...';
            selectedOption.classList.add('placeholder');
        }

        optionsList.querySelectorAll('.option').forEach(opt => {
            opt.classList.toggle('selected', opt.dataset.value === nativeSelect.value);
        });
    });
}

/**
 * Update the select value and UI
 * @param {HTMLElement} nativeSelect - The native select element
 * @param {string} value - The value to set
 * @param {HTMLElement} optionElement - The option element that was clicked
 * @param {HTMLElement} optionsList - The options list dropdown element
 * @param {string} text - The displayed text
 */
function updateSelectValue(nativeSelect, value, optionElement, optionsList, text) {
    nativeSelect.value = value;
    nativeSelect.dispatchEvent(new Event('change'));

    const selectedOption = optionElement.closest('.custom-select-wrapper').querySelector('.selected-option');
    selectedOption.textContent = text;
    selectedOption.classList.remove('placeholder');

    optionsList.querySelectorAll('.option').forEach(opt => {
        opt.classList.remove('selected');
    });
    optionElement.classList.add('selected');

    optionsList.classList.remove('show');
    selectedOption.classList.remove('active');
}
