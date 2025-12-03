import colorPalette from "@/constants/colorPalette";
import { cx, css } from "@emotion/css";
import { Link } from "react-router-dom";

const LinkRenderer = ({
  href,
  children,
  target = "_self",
  includeLinkStyles = false,
  className,
}: {
  href: string;
  children: React.ReactNode;
  target?: "_blank" | "_self" | "_parent" | "_top";
  className?: string;
  includeLinkStyles?: boolean;
}) => {
  if (!href) return children;

  const isExternal = href.startsWith("http") || href.startsWith("mailto:");

  if (isExternal) {
    return (
      <a
        href={href}
        target={target ?? "_blank"}
        rel={target === "_blank" ? "noopener noreferrer" : undefined}
        className={cx(
          className,
          css`
            ${includeLinkStyles &&
            css`
              color: ${colorPalette.white};
              text-decoration: underline;
            `}
          `
        )}
      >
        {children}
      </a>
    );
  }

  return (
    <Link
      to={href}
      target={target}
      className={cx(
        className,
        css`
          ${includeLinkStyles &&
          css`
            color: ${colorPalette.white};
            text-decoration: underline;
          `}
        `
      )}
    >
      {children}
    </Link>
  );
};

export default LinkRenderer;
