import { css } from "@emotion/css";
import colorPalette from "@/constants/colorPalette";

export const skeletonAnimation = css`
  animation: skeleton-loading 1s linear infinite alternate;

  @keyframes skeleton-loading {
    0% {
      background-color: ${colorPalette.background};
    }
    100% {
      background-color: ${colorPalette.backgroundBright};
    }
  }
`;
