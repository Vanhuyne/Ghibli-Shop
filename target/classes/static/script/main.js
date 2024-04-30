    let currentSlide = 0;
    const slides = document.querySelectorAll('.slide');
    const totalSlides = slides.length;
    let slideInterval;

    const showSlide = (n) => {
      slides[currentSlide].classList.remove('active');
      currentSlide = (n + totalSlides) % totalSlides;
      slides[currentSlide].classList.add('active');
    }

    const nextSlide = () => showSlide(currentSlide + 1);

    const prevSlide = () => showSlide(currentSlide - 1);

    const startSlideShow = () => slideInterval = setInterval(nextSlide, 5000); // Change slide every 5 seconds

    const stopSlideShow = () => clearInterval(slideInterval);

    document.getElementById('prevBtn').addEventListener('click', prevSlide);
    document.getElementById('nextBtn').addEventListener('click', nextSlide);

    // Pause the slideshow when the mouse is over the slider
    document.querySelector('.slider-container').addEventListener('mouseenter', stopSlideShow);

    // Restart the slide show when the mouse leaves the slider
    document.querySelector('.slider-container').addEventListener('mouseleave', startSlideShow);

    // Show the first slide and start the slide show when the page loads
    showSlide(currentSlide);
    startSlideShow();

//// smooth
//$(document).ready(function(){
//  // Add smooth scrolling to all links
//  $("a").on('click', function(event) {
//
//    // Make sure this.hash has a value before overriding default behavior
//    if (this.hash !== "") {
//      // Prevent default anchor click behavior
//      event.preventDefault();
//
//      // Store hash
//      var hash = this.hash;
//
//      // Using jQuery's animate() method to add smooth page scroll
//      // The optional number (800) specifies the number of milliseconds it takes to scroll to the specified area
//      $('html, body').animate({
//        scrollTop: $(hash).offset().top
//      }, 800, function(){
//
//        // Add hash (#) to URL when done scrolling (default click behavior)
//        window.location.hash = hash;
//      });
//    } // End if
//  });
//});