import { useState, useEffect } from "react";
import breakPoints from "@/constants/breakPoints";

interface ViewportSize {
  width: number;
  height: number;
}

interface ViewportFlags {
  isMobile: boolean;
  isTablet: boolean;
  isLaptop: boolean;
  isDesktop: boolean;

  isTabletOrSmaller: boolean;
  isLaptopOrSmaller: boolean;
  isTabletOrLarger: boolean;
}

interface UseViewportReturn extends ViewportSize, ViewportFlags {}

const useViewport = (): UseViewportReturn => {
  const [size, setSize] = useState<ViewportSize>({
    width: typeof window !== "undefined" ? window.innerWidth : 0,
    height: typeof window !== "undefined" ? window.innerHeight : 0,
  });

  useEffect(() => {
    const handleResize = () => {
      setSize({
        width: window.innerWidth,
        height: window.innerHeight,
      });
    };

    window.addEventListener("resize", handleResize);

    // Cleanup
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const { width } = size;

  // Flagi breakpointów (mobile-first)
  const isMobile = width < breakPoints.tablet;
  const isTablet = width >= breakPoints.tablet && width < breakPoints.laptop;
  const isLaptop = width >= breakPoints.laptop && width < breakPoints.desktop;
  const isDesktop = width >= breakPoints.desktop;

  // Pomocnicze flagi
  const isTabletOrSmaller = width < breakPoints.laptop;
  const isLaptopOrSmaller = width < breakPoints.desktop;
  const isTabletOrLarger = width >= breakPoints.tablet;

  return {
    ...size,
    isMobile,
    isTablet,
    isLaptop,
    isDesktop,
    isTabletOrSmaller,
    isLaptopOrSmaller,
    isTabletOrLarger,
  };
};

export default useViewport;
