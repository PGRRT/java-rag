import IconWrapper from "@/components/ui/IconWrapper";
import React from "react";
import { ArrowLeft, ChevronLeft, House } from "lucide-react";
import colorPalette from "@/constants/colorPalette";

const Logo = ({
  onClick,
  size = 44,
}: {
  onClick?: () => void;
  size?: number;
}) => {
  return (
    <>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 24 24"
        fill="none"
        width={size}
        height={size}
        onClick={onClick}
        style={{ cursor: onClick ? "pointer" : "default" }}
      >
        <path
          d="M12 3v18 M10 5h4"
          stroke="#ffffff"
          strokeWidth="2"
          strokeLinecap="round"
        />

        <path
          d="M15 20c0-1.5-1-2.5-3-3.5s-3-2-3-3.5c0-1.5 1-2.5 3-3.5s3-2 3-3.5c0-1.5-1-2.5-3-3.5"
          stroke="#06b6d4"
          strokeWidth="2"
          strokeLinecap="round"
          strokeDasharray="2 1"
        />
        <circle cx="9" cy="3" r="1.5" fill="#06b6d4" />

        <circle cx="12" cy="10" r="1" fill="#06b6d4" />
        <circle cx="12" cy="17" r="1" fill="#06b6d4" />
      </svg>
    </>
  );
};

export default Logo;

// snake on cross
{
  /* <svg
  xmlns="http://www.w3.org/2000/svg"
  viewBox="0 0 24 24"
  fill="none"
  width={size}
  height={size}
  onClick={onClick}
  style={{ cursor: onClick ? "pointer" : "default" }}
>
  <path
    d="M12 3v18 M10 5h4"
    stroke="#ffffff"
    strokeWidth="2"
    strokeLinecap="round"
  />

  <path
    d="M15 20c0-1.5-1-2.5-3-3.5s-3-2-3-3.5c0-1.5 1-2.5 3-3.5s3-2 3-3.5c0-1.5-1-2.5-3-3.5"
    stroke="#06b6d4"
    strokeWidth="2"
    strokeLinecap="round"
    strokeDasharray="2 1"
  />
  <circle cx="9" cy="3" r="1.5" fill="#06b6d4" />

  <circle cx="12" cy="10" r="1" fill="#06b6d4" />
  <circle cx="12" cy="17" r="1" fill="#06b6d4" />
</svg>
 */
}

/* chart */
// <svg
//   xmlns="http://www.w3.org/2000/svg"
//   viewBox="0 0 24 24"
//   fill="none"
//   stroke="#60a5fa" /* Niebieski */
//   strokeWidth="2"
//   strokeLinecap="round"
//   strokeLinejoin="round"
//   width="40"
//   height="40"
// >
//   <path d="M22 12h-4l-3 9L9 3l-3 9H2" />
//   <rect x="15" y="5" width="6" height="6" rx="1" strokeOpacity="0.5" />
//   <circle cx="18" cy="8" r="1" fill="currentColor" />
// </svg>

/* shield */
/*
 <svg
  xmlns="http://www.w3.org/2000/svg"
  viewBox="0 0 24 24"
  fill="none"
  width="50"
  height="50"
>
  <path
    d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"
    stroke="#ffffff"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
  />
  
  <path d="M12 8v8" stroke="#06b6d4" strokeWidth="2" strokeLinecap="round"/>
  <path d="M8 12h8" stroke="#06b6d4" strokeWidth="2" strokeLinecap="round"/>
  
  <circle cx="12" cy="8" r="1.5" fill="#06b6d4" />
  <circle cx="12" cy="16" r="1.5" fill="#06b6d4" />
  <circle cx="8" cy="12" r="1.5" fill="#06b6d4" />
  <circle cx="16" cy="12" r="1.5" fill="#06b6d4" />
</svg>
 */
