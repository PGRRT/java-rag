// JavaScript for toggling the aside menu and animating the menu button
document.addEventListener('DOMContentLoaded', function() {
    const aside = document.querySelector('aside');
    const menuButton = document.getElementById('menu-button');
    const menuButtonImage = document.getElementById('menu-button-image');
    const asideContent = document.getElementById('aside-content');

    menuButton.addEventListener('click', function() {
        aside.classList.toggle('aside-expanded');
        menuButtonImage.classList.toggle('open');
        asideContent.style.display = aside.classList.contains('aside-expanded') ? 'block' : 'none';
    });
});
