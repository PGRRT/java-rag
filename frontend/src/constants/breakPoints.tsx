// Breakpoints w px (mobile-first approach)
export const breakPoints = {
  mobile: 0,
  tablet: 768, 
  laptop: 1024, 
  desktop: 1440,
} as const;

// Media queries dla CSS-in-JS / Emotion
export const breakPointsMediaQueries = {
  mobile: `@media (max-width: ${breakPoints.tablet - 1}px)`, 
  tablet: `@media (min-width: ${breakPoints.tablet}px)`, 
  laptop: `@media (min-width: ${breakPoints.laptop}px)`, 
  desktop: `@media (min-width: ${breakPoints.desktop}px)`,

  onlyMobile: `@media (max-width: ${breakPoints.tablet - 1}px)`,
  onlyTablet: `@media (min-width: ${breakPoints.tablet}px) and (max-width: ${
    breakPoints.laptop - 1
  }px)`,
  onlyLaptop: `@media (min-width: ${breakPoints.laptop}px) and (max-width: ${
    breakPoints.desktop - 1
  }px)`,
} as const;

export default breakPoints;
