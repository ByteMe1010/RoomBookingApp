class NngfCardPane extends HTMLElement {
    connectedCallback() {
        this.showPage(parseInt(this.getAttribute('selected')) || 0);
    }

    static get observedAttributes() {
        return ['selected'];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if (name === 'selected' && oldValue !== newValue) {
            this.showPage(parseInt(newValue) || 0);
        }
    }

    showPage(index) {
        var children = this.children;
        for (var i = 0; i < children.length; i++) {
            children[i].style.display = i === index ? '' : 'none';
        }
    }

    showPageById(id) {
        var children = this.children;
        for (var i = 0; i < children.length; i++) {
            children[i].style.display = children[i].id === id ? '' : 'none';
        }
    }
}

customElements.define('nngf-cardpane', NngfCardPane);
